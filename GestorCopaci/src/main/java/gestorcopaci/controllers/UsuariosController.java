package gestorcopaci.controllers;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import gestorcopaci.daos.AsistenciaDAO;
import gestorcopaci.daos.CiudadanoDAO;
import gestorcopaci.daos.CooperacionDAO;
import gestorcopaci.daos.FaenaDAO;
import gestorcopaci.daos.VocalDAO;
import gestorcopaci.models.Ciudadano;
import gestorcopaci.models.Cooperacion;
import gestorcopaci.models.Faena;
import gestorcopaci.models.Vocal;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class UsuariosController {

    // ==== CLASE INTERNA PARA MOSTRAR FAENAS POR MES ====
    public static class FaenaResumen {
        private final int anio;
        private final String mes;
        private final String estado;
        private final double monto;

        public FaenaResumen(int anio, String mes, String estado, double monto) {
            this.anio = anio;
            this.mes = mes;
            this.estado = estado;
            this.monto = monto;
        }

        public int getAnio() { return anio; }
        public String getMes() { return mes; }
        public String getEstado() { return estado; }
        public double getMonto() { return monto; }
    }

    // BUSCADOR
    @FXML private TextField txtBuscar;
    @FXML private TableView<Ciudadano> tblCiudadanos;
    @FXML private TableColumn<Ciudadano, Integer> colId;
    @FXML private TableColumn<Ciudadano, String> colNombre;
    @FXML private TableColumn<Ciudadano, String> colManzana;
    @FXML private TableColumn<Ciudadano, String> colTipo;

    // DATOS PERSONALES
    @FXML private Label lblId;
    @FXML private Label lblNombre;
    @FXML private Label lblFechaNac;
    @FXML private Label lblEdad;
    @FXML private Label lblDomicilio;
    @FXML private Label lblManzana;
    @FXML private Label lblEstadoCivil;
    @FXML private Label lblOcupacion;
    @FXML private Label lblTipoCiudadano;
    @FXML private Label lblFechaAlta;
    @FXML private Label lblCertificado;
    @FXML private Label lblLugarNacimiento;
    @FXML private Label lblGradoEstudios;

    // FAENAS (USAMOS FaenaResumen)
    @FXML private TableView<FaenaResumen> tblFaenas;
    @FXML private TableColumn<FaenaResumen, Integer> colFaenaAnio;
    @FXML private TableColumn<FaenaResumen, String> colFaenaMes;
    @FXML private TableColumn<FaenaResumen, String> colFaenaAsistencia;
    @FXML private TableColumn<FaenaResumen, Number> colFaenaPago;
    @FXML private TableColumn<FaenaResumen, String> colFaenaFecha; // aquí pondremos vacío
    @FXML private Label lblResumenFaenas;

    // COOPERACIONES
    @FXML private TableView<Cooperacion> tblCooperaciones;
    @FXML private TableColumn<Cooperacion, Integer> colCoopAnio;
    @FXML private TableColumn<Cooperacion, String> colCoopTipo;
    @FXML private TableColumn<Cooperacion, Number> colCoopTotal;
    @FXML private TableColumn<Cooperacion, String> colCoopFecha;
    @FXML private Label lblResumenCoops;

    // VOCAL / ASISTENCIA
    @FXML private Label lblEstadoVocal;
    @FXML private Label lblPeriodoVocal;
    @FXML private Label lblEventosAsistidos;
    @FXML private Label lblPorcentajeAsistencia;
    @FXML private Label lblMensajeVocal;

    // DAOs
    private CiudadanoDAO ciudadanoDAO;
    private FaenaDAO faenaDAO;
    private CooperacionDAO cooperacionDAO;
    private VocalDAO vocalDAO;
    private AsistenciaDAO asistenciaDAO;

    private final ObservableList<Ciudadano> listaCiudadanos = FXCollections.observableArrayList();
    private final ObservableList<FaenaResumen> listaFaenas = FXCollections.observableArrayList();
    private final ObservableList<Cooperacion> listaCooperaciones = FXCollections.observableArrayList();

    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    private void initialize() {
        ciudadanoDAO = new CiudadanoDAO();
        faenaDAO = new FaenaDAO();
        cooperacionDAO = new CooperacionDAO();
        vocalDAO = new VocalDAO();
        asistenciaDAO = new AsistenciaDAO();

        configurarTablaCiudadanos();
        configurarTablaFaenas();
        configurarTablaCooperaciones();

        cargarCiudadanos();
        configurarFiltroBusqueda();
        configurarSeleccion();
        limpiarExpediente();
    }

    private void configurarTablaCiudadanos() {
        colId.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getIdCiudadano()).asObject());
        colNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombre()));
        colManzana.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getManzana()));
        colTipo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTipoCiudadano()));
    }

    private void configurarTablaFaenas() {
        colFaenaAnio.setCellValueFactory(f -> new SimpleIntegerProperty(f.getValue().getAnio()).asObject());
        colFaenaMes.setCellValueFactory(f -> new SimpleStringProperty(f.getValue().getMes()));
        colFaenaAsistencia.setCellValueFactory(f -> new SimpleStringProperty(f.getValue().getEstado()));
        colFaenaPago.setCellValueFactory(f -> new SimpleDoubleProperty(f.getValue().getMonto()));
        colFaenaFecha.setCellValueFactory(f -> new SimpleStringProperty("")); // no manejamos fecha por mes

        tblFaenas.setItems(listaFaenas);
    }

    private void configurarTablaCooperaciones() {
        colCoopAnio.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getAnio()).asObject());
        colCoopTipo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTipoPago()));
        colCoopTotal.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getTotalPagado()));
        colCoopFecha.setCellValueFactory(c -> {
            LocalDate fecha = c.getValue().getFechaRegistro();
            String txt = (fecha != null) ? fecha.format(fmt) : "";
            return new SimpleStringProperty(txt);
        });

        tblCooperaciones.setItems(listaCooperaciones);
    }

    private void cargarCiudadanos() {
        listaCiudadanos.clear();
        List<Ciudadano> todos = ciudadanoDAO.obtenerTodosCiudadanos();
        if (todos != null) {
            listaCiudadanos.addAll(todos);
        }
    }

    private void configurarFiltroBusqueda() {
        FilteredList<Ciudadano> filtrados = new FilteredList<>(listaCiudadanos, c -> true);

        txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> {
            String filtro = (newVal == null) ? "" : newVal.trim().toLowerCase(Locale.ROOT);
            filtrados.setPredicate(c -> {
                if (filtro.isEmpty()) return true;
                String nombre = c.getNombre() != null ? c.getNombre().toLowerCase(Locale.ROOT) : "";
                return nombre.contains(filtro);
            });
        });

        SortedList<Ciudadano> ordenados = new SortedList<>(filtrados);
        ordenados.comparatorProperty().bind(tblCiudadanos.comparatorProperty());
        tblCiudadanos.setItems(ordenados);
    }

    private void configurarSeleccion() {
        tblCiudadanos.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSel, nuevo) -> {
                    if (nuevo != null) {
                        mostrarExpediente(nuevo);
                    } else {
                        limpiarExpediente();
                    }
                }
        );
    }

    private void mostrarExpediente(Ciudadano c) {
        // DATOS PERSONALES
        lblId.setText(String.valueOf(c.getIdCiudadano()));
        lblNombre.setText(safe(c.getNombre()));
        if (c.getFechaNacimiento() != null) {
            lblFechaNac.setText(c.getFechaNacimiento().format(fmt));
            int edad = Period.between(c.getFechaNacimiento(), LocalDate.now()).getYears();
            lblEdad.setText(edad + " años");
        } else {
            lblFechaNac.setText("-");
            lblEdad.setText("-");
        }
        lblDomicilio.setText(safe(c.getDomicilioActual()));
        lblManzana.setText(safe(c.getManzana()));
        lblEstadoCivil.setText(safe(c.getEstadoCivil()));
        lblOcupacion.setText(safe(c.getOcupacion()));
        lblTipoCiudadano.setText(safe(c.getTipoCiudadano()));
        if (c.getFechaAlta() != null) {
            lblFechaAlta.setText(c.getFechaAlta().format(fmt));
        } else {
            lblFechaAlta.setText("-");
        }
        lblCertificado.setText(safe(c.getTipoCertificado()));
        lblLugarNacimiento.setText(safe(c.getLugarNacimiento()));
        lblGradoEstudios.setText(safe(c.getGradoEstudios()));

        // FAENAS -> RESUMEN MENSUAL
        llenarResumenFaenas(c);

        // COOPERACIONES
        List<Cooperacion> cooperaciones = cooperacionDAO.obtenerCooperacionesPorCiudadano(c.getIdCiudadano());
        listaCooperaciones.setAll(cooperaciones);
        double totalCoops = cooperaciones.stream()
                .mapToDouble(Cooperacion::getTotalPagado)
                .sum();
        lblResumenCoops.setText("Total registros: " + cooperaciones.size() +
                " | Monto acumulado: $" + String.format(Locale.US, "%.2f", totalCoops));

        // VOCAL / ASISTENCIAS
        Vocal vocal = vocalDAO.obtenerVocalPorCiudadano(c.getIdCiudadano());
        if (vocal != null) {
            lblEstadoVocal.setText("Es vocal (" + vocal.getEstado() + ")");
            lblPeriodoVocal.setText(
                    (vocal.getFechaInicioMandato() != null ? vocal.getFechaInicioMandato().toString() : "?")
                            + "  -  " +
                    (vocal.getFechaFinMandato() != null ? vocal.getFechaFinMandato().toString() : "?")
            );
            lblEventosAsistidos.setText(String.valueOf(vocal.getEventosAsistidos()));
            lblPorcentajeAsistencia.setText(
                    String.format(Locale.US, "%.2f %%", vocal.getPorcentajeAsistencia())
            );

            if (vocal.getPorcentajeAsistencia() >= 90.0) {
                lblMensajeVocal.setText("→ CUMPLIÓ COMO VOCAL DE COPACI");
            } else {
                lblMensajeVocal.setText("");
            }
        } else {
            lblEstadoVocal.setText("No es vocal");
            lblPeriodoVocal.setText("-");
            lblEventosAsistidos.setText("-");
            lblPorcentajeAsistencia.setText("-");
            lblMensajeVocal.setText("");
        }
    }

    /**
     * Genera SIEMPRE 12 filas (enero-diciembre) para el año actual,
     * con estado: Pagado / No pagado / No aplica (vocal).
     */
    private void llenarResumenFaenas(Ciudadano c) {
        listaFaenas.clear();

        int anioActual = LocalDate.now().getYear();

        // Traemos faenas reales desde BD (si las tienes)
        List<Faena> faenasDb = faenaDAO.obtenerFaenasPorCiudadanoYAnio(c.getIdCiudadano(), anioActual);
        Map<String, Faena> porMes = faenasDb.stream()
                .collect(Collectors.toMap(
                        f -> f.getMes().toLowerCase(Locale.ROOT),
                        f -> f,
                        (a, b) -> a
                ));

        // ¿Es vocal activo?
        boolean esVocal = vocalDAO.esVocalActivo(c.getIdCiudadano());

        String[] meses = {
                "Enero", "Febrero", "Marzo", "Abril",
                "Mayo", "Junio", "Julio", "Agosto",
                "Septiembre", "Octubre", "Noviembre", "Diciembre"
        };

        int countPagado = 0;
        int countNoPagado = 0;
        int countNoAplica = 0;

        for (String mes : meses) {
            String mesKey = mes.toLowerCase(Locale.ROOT);
            String estado;
            double monto;

            if (esVocal) {
                estado = "No aplica (vocal)";
                monto = 0.0;
                countNoAplica++;
            } else {
                Faena f = porMes.get(mesKey);
                if (f != null) {
                    // Si asistió o pagó reposición -> lo consideramos "Pagado"
                    if (f.isAsistencia() || f.getPagoReposicion() > 0.0) {
                        estado = "Pagado";
                        monto = f.getPagoReposicion();
                        countPagado++;
                    } else {
                        // registro sin asistencia y sin pago
                        estado = "No pagado";
                        monto = 0.0;
                        countNoPagado++;
                    }
                } else {
                    // No existe registro en BD para este mes -> No pagado
                    estado = "No pagado";
                    monto = 0.0;
                    countNoPagado++;
                }
            }

            listaFaenas.add(new FaenaResumen(anioActual, mes, estado, monto));
        }

        lblResumenFaenas.setText(
                "Pagado: " + countPagado +
                " | No pagado: " + countNoPagado +
                " | No aplica (vocal): " + countNoAplica
        );
    }

    private void limpiarExpediente() {
        lblId.setText("-");
        lblNombre.setText("-");
        lblFechaNac.setText("-");
        lblEdad.setText("-");
        lblDomicilio.setText("-");
        lblManzana.setText("-");
        lblEstadoCivil.setText("-");
        lblOcupacion.setText("-");
        lblTipoCiudadano.setText("-");
        lblFechaAlta.setText("-");
        lblCertificado.setText("-");
        lblLugarNacimiento.setText("-");
        lblGradoEstudios.setText("-");

        listaFaenas.clear();
        listaCooperaciones.clear();
        lblResumenFaenas.setText("Sin registros.");
        lblResumenCoops.setText("Sin registros.");

        lblEstadoVocal.setText("-");
        lblPeriodoVocal.setText("-");
        lblEventosAsistidos.setText("-");
        lblPorcentajeAsistencia.setText("-");
        lblMensajeVocal.setText("");
    }

    private String safe(String s) {
        return (s == null || s.isBlank()) ? "-" : s;
    }
}
