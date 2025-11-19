package gestorcopaci.controllers;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import gestorcopaci.daos.AsistenciaDAO;
import gestorcopaci.daos.CiudadanoDAO;
import gestorcopaci.daos.CooperacionDAO;
import gestorcopaci.daos.FaenaDAO;
import gestorcopaci.daos.VocalDAO;
import gestorcopaci.models.Ciudadano;
import gestorcopaci.models.Cooperacion;
import gestorcopaci.models.Faena;
import gestorcopaci.models.Vocal;
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

    // FAENAS
    @FXML private TableView<Faena> tblFaenas;
    @FXML private TableColumn<Faena, Integer> colFaenaAnio;
    @FXML private TableColumn<Faena, String> colFaenaMes;
    @FXML private TableColumn<Faena, String> colFaenaAsistencia;
    @FXML private TableColumn<Faena, Number> colFaenaPago;
    @FXML private TableColumn<Faena, String> colFaenaFecha;
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
    private final ObservableList<Faena> listaFaenas = FXCollections.observableArrayList();
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
        colId.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getIdCiudadano()).asObject());
        colNombre.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getNombre()));
        colManzana.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getManzana()));
        colTipo.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getTipoCiudadano()));
    }

    private void configurarTablaFaenas() {
        colFaenaAnio.setCellValueFactory(f -> new javafx.beans.property.SimpleIntegerProperty(f.getValue().getAnio()).asObject());
        colFaenaMes.setCellValueFactory(f -> new javafx.beans.property.SimpleStringProperty(f.getValue().getMes()));
        colFaenaAsistencia.setCellValueFactory(f -> new javafx.beans.property.SimpleStringProperty(
                f.getValue().isAsistencia() ? "Asistió" : "No asistió"));
        colFaenaPago.setCellValueFactory(f -> new javafx.beans.property.SimpleDoubleProperty(f.getValue().getPagoReposicion()));
        colFaenaFecha.setCellValueFactory(f -> {
            LocalDate fecha = f.getValue().getFechaRegistro();
            String txt = (fecha != null) ? fecha.format(fmt) : "";
            return new javafx.beans.property.SimpleStringProperty(txt);
        });

        tblFaenas.setItems(listaFaenas);
    }

    private void configurarTablaCooperaciones() {
        colCoopAnio.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getAnio()).asObject());
        colCoopTipo.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getTipoPago()));
        colCoopTotal.setCellValueFactory(c -> new javafx.beans.property.SimpleDoubleProperty(c.getValue().getTotalPagado()));
        colCoopFecha.setCellValueFactory(c -> {
            LocalDate fecha = c.getValue().getFechaRegistro();
            String txt = (fecha != null) ? fecha.format(fmt) : "";
            return new javafx.beans.property.SimpleStringProperty(txt);
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

        // FAENAS
        List<Faena> faenas = faenaDAO.obtenerFaenasPorCiudadano(c.getIdCiudadano());
        listaFaenas.setAll(faenas);
        long totalFaenas = faenas.size();
        long asistidas = faenas.stream().filter(Faena::isAsistencia).count();
        lblResumenFaenas.setText("Total registros: " + totalFaenas +
                " | Asistidas: " + asistidas +
                " | No asistió (paga): " + (totalFaenas - asistidas));

        // COOPERACIONES
        List<Cooperacion> cooperaciones = cooperacionDAO.obtenerCooperacionesPorCiudadano(c.getIdCiudadano());
        listaCooperaciones.setAll(cooperaciones);
        double totalCoops = cooperaciones.stream()
                .mapToDouble(Cooperacion::getTotalPagado)
                .sum();
        lblResumenCoops.setText("Total de cooperaciones registradas: " + cooperaciones.size() +
                " | Monto acumulado: $" + String.format(Locale.US, "%.2f", totalCoops));

        // VOCAL / ASISTENCIAS
        Vocal vocal = vocalDAO.obtenerVocalPorCiudadano(c.getIdCiudadano());
        if (vocal != null) {
            lblEstadoVocal.setText("Es vocal (" + vocal.getEstado() + ")");
            lblPeriodoVocal.setText(vocal.getFechaInicioMandato() + "  -  " + vocal.getFechaFinMandato());
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
