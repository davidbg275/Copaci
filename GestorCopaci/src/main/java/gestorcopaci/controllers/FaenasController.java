package gestorcopaci.controllers;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import gestorcopaci.daos.CiudadanoDAO;
import gestorcopaci.daos.FaenaDAO;
import gestorcopaci.daos.VocalDAO;
import gestorcopaci.models.Ciudadano;
import gestorcopaci.models.Faena;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class FaenasController {

    // ---------- CONTROLES FXML ----------

    @FXML
    private TextField txtIdCiudadano;
    @FXML
    private Label lblNombreCiudadano;
    @FXML
    private Label lblTipoCiudadano;

    @FXML
    private ComboBox<Integer> cbAnio;
    @FXML
    private ComboBox<String> cbMes;
    @FXML
    private CheckBox chkAsistencia;
    @FXML
    private DatePicker dpFechaRegistro;
    @FXML
    private Label lblPagoReposicion;

    @FXML
    private TableView<Faena> tablaFaenas;
    @FXML
    private TableColumn<Faena, Integer> colAnio;
    @FXML
    private TableColumn<Faena, String> colMes;
    @FXML
    private TableColumn<Faena, String> colAsistencia;
    @FXML
    private TableColumn<Faena, String> colPago;
    @FXML
    private TableColumn<Faena, String> colFecha;

    @FXML
    private ListView<String> listaAdeudos;
    @FXML
    private Label lblTotalAdeudo;

    @FXML
    private Button btnGuardar;
    @FXML
    private Button btnLimpiar;
    @FXML
    private Button btnCancelar;
    @FXML
    private Button btnPagarSeleccionados;
    @FXML
    private Button btnPagarTodos;

    // ---------- CAMPOS LÓGICOS ----------

    private FaenaDAO faenaDAO;
    private CiudadanoDAO ciudadanoDAO;
    private VocalDAO vocalDAO;
    private Ciudadano ciudadanoSeleccionado;

    // ---------- CAMPOS LÓGICOS ----------

    private static final List<String> MESES = Arrays.asList(
            "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre");

    // ---------- INITIALIZE ----------

    @FXML

private void initialize() {
    faenaDAO = new FaenaDAO();
    ciudadanoDAO = new CiudadanoDAO();
    vocalDAO = new VocalDAO();

    int anioActual = LocalDate.now().getYear();

    // Años - NO cargar años aquí, se cargarán cuando se seleccione un ciudadano
    if (cbAnio != null) {
        // Solo inicializar el combobox vacío
        cbAnio.valueProperty().addListener((obs, o, n) -> cargarFaenas());
    }

    // Meses
    if (cbMes != null) {
        cbMes.getItems().addAll(MESES);
        cbMes.getSelectionModel().select(LocalDate.now().getMonthValue() - 1);
    }

    // El resto del código initialize permanece igual...
    // Fecha
    if (dpFechaRegistro != null) {
        dpFechaRegistro.setValue(LocalDate.now());
    }

    // Tabla de faenas
    if (tablaFaenas != null) {
        colAnio.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getAnio()));
        colMes.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getMes()));
        colAsistencia.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().isAsistencia() ? "Sí" : "No"));
        colPago.setCellValueFactory(
                c -> new SimpleStringProperty(String.format("$ %.2f", c.getValue().getPagoReposicion())));
        colFecha.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getFechaRegistro() != null
                        ? c.getValue().getFechaRegistro().toString()
                        : ""));
    }

    // Lista de adeudos: selección múltiple
    if (listaAdeudos != null) {
        listaAdeudos.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listaAdeudos.getItems().clear();
    }
    if (lblTotalAdeudo != null) {
        lblTotalAdeudo.setText("$ 0.00");
    }

    // Listeners
    if (txtIdCiudadano != null) {
        txtIdCiudadano.textProperty().addListener((obs, oldVal, newVal) -> buscarCiudadano());
    }
    if (chkAsistencia != null) {
        chkAsistencia.selectedProperty().addListener((obs, oldVal, newVal) -> actualizarPagoReposicion());
    }

    actualizarPagoReposicion();
}

    // ---------- LÓGICA PRINCIPAL ----------

private void buscarCiudadano() {
    if (txtIdCiudadano == null)
        return;

    String idText = txtIdCiudadano.getText().trim();
    ciudadanoSeleccionado = null;

    if (lblNombreCiudadano != null) {
        lblNombreCiudadano.setText("");
    }
    if (lblTipoCiudadano != null) {
        lblTipoCiudadano.setText("");
    }
    if (tablaFaenas != null) {
        tablaFaenas.getItems().clear();
    }
    limpiarAdeudos();
    actualizarPagoReposicion();

    if (idText.isEmpty()) {
        return;
    }

    try {
        int id = Integer.parseInt(idText);
        List<Ciudadano> ciudadanos = ciudadanoDAO.obtenerTodosCiudadanos();

        for (Ciudadano c : ciudadanos) {
            if (c.getIdCiudadano() == id) {
                ciudadanoSeleccionado = c;

                if (lblNombreCiudadano != null) {
                    lblNombreCiudadano.setText("Ciudadano: " + c.getNombre());
                    lblNombreCiudadano.setStyle("-fx-text-fill: #4caf50; -fx-font-weight: bold;");
                }

                String tipo = c.getTipoCiudadano() != null ? c.getTipoCiudadano() : "Ciudadano";
                String tipoLower = tipo.toLowerCase();

                String detalle;
                if (tipoLower.contains("vocal")) {
                    detalle = "Vocal (100% descuento en faenas)";
                } else if (tipoLower.contains("estudiante") || tipoLower.contains("madre")) {
                    detalle = tipo + " (50% descuento en faenas)";
                } else {
                    detalle = tipo + " (paga faena completa)";
                }

                if (lblTipoCiudadano != null) {
                    lblTipoCiudadano.setText(detalle);
                }

                // ACTUALIZAR AÑOS DISPONIBLES BASADO EN CUÁNDO CUMPLIÓ 18 AÑOS
                actualizarAniosDisponibles();

                cargarFaenas();
                actualizarPagoReposicion();
                return;
            }
        }

        if (lblNombreCiudadano != null) {
            lblNombreCiudadano.setText("Ciudadano no encontrado");
            lblNombreCiudadano.setStyle("-fx-text-fill: #f44336; -fx-font-weight: bold;");
        }

    } catch (NumberFormatException e) {
        if (lblNombreCiudadano != null) {
            lblNombreCiudadano.setText("ID inválido");
            lblNombreCiudadano.setStyle("-fx-text-fill: #f44336; -fx-font-weight: bold;");
        }
    }
}

/**
 * Actualiza los años disponibles en el combobox basado en cuándo el ciudadano cumplió 18 años
 */
private void actualizarAniosDisponibles() {
    if (cbAnio == null || ciudadanoSeleccionado == null) {
        return;
    }

    // Limpiar años actuales
    cbAnio.getItems().clear();

    LocalDate hoy = LocalDate.now();
    int anioActual = hoy.getYear();

    // Calcular año en que cumplió 18 años
    LocalDate nacimiento = ciudadanoSeleccionado.getFechaNacimiento();
    if (nacimiento == null) {
        // Si no tiene fecha de nacimiento, mostrar últimos 5 años
        for (int a = anioActual - 5; a <= anioActual + 1; a++) {
            cbAnio.getItems().add(a);
        }
        cbAnio.getSelectionModel().select(Integer.valueOf(anioActual));
        return;
    }

    LocalDate fechaCumple18 = nacimiento.plusYears(18);
    int anioCumple18 = fechaCumple18.getYear();

    // Si aún no cumple 18 años, no mostrar años
    if (fechaCumple18.isAfter(hoy)) {
        cbAnio.getItems().clear();
        return;
    }

    // Mostrar años desde que cumplió 18 hasta el año actual + 1 (para futuras faenas)
    for (int anio = anioCumple18; anio <= anioActual + 1; anio++) {
        cbAnio.getItems().add(anio);
    }

    // Seleccionar el año actual por defecto (si está disponible)
    if (cbAnio.getItems().contains(anioActual)) {
        cbAnio.getSelectionModel().select(Integer.valueOf(anioActual));
    } else if (!cbAnio.getItems().isEmpty()) {
        // Si el año actual no está disponible, seleccionar el último año disponible
        cbAnio.getSelectionModel().selectLast();
    }
}

    private void actualizarPagoReposicion() {
        double pago = 0.0;

        if (chkAsistencia != null && !chkAsistencia.isSelected()) {
            String tipo = (ciudadanoSeleccionado != null) ? ciudadanoSeleccionado.getTipoCiudadano() : null;
            pago = calcularPagoReposicion(tipo);
        }

        if (lblPagoReposicion != null) {
            lblPagoReposicion.setText(String.format("$ %.2f", pago));
        }
    }

    /**
     * Descuentos:
     * - Vocal activo: 100% desc → 0
     * - Estudiante / Madre Soltera: 50% desc → 50
     * - Normal: 100
     */
    private double calcularPagoReposicion(String tipoCiudadano) {
        final double BASE = 100.0;

        // 1) Si el ciudadano es vocal activo, no paga faena
        try {
            if (ciudadanoSeleccionado != null && vocalDAO != null) {
                boolean esVocal = vocalDAO.esVocalActivo(ciudadanoSeleccionado.getIdCiudadano());
                if (esVocal) {
                    return 0.0;
                }
            }
        } catch (Exception ex) {
            System.err.println("Error comprobando vocal activo: " + ex.getMessage());
            // Si hay error, sigue con la lógica normal de tipo_ciudadano
        }

        // 2) Si no es vocal, aplicamos reglas por tipo de ciudadano
        if (tipoCiudadano == null || tipoCiudadano.isEmpty()) {
            return BASE;
        }

        String tipo = tipoCiudadano.toLowerCase();

        if (tipo.contains("estudiante") || tipo.contains("madre")) {
            return BASE * 0.5;
        }

        return BASE;
    }

    private void cargarFaenas() {
        if (tablaFaenas == null || ciudadanoSeleccionado == null || cbAnio == null) {
            limpiarAdeudos();
            return;
        }

        tablaFaenas.getItems().clear();

        Integer anio = cbAnio.getValue();
        if (anio == null) {
            limpiarAdeudos();
            return;
        }

        List<Faena> lista = faenaDAO.obtenerFaenasPorCiudadanoYAnio(
                ciudadanoSeleccionado.getIdCiudadano(),
                anio);
        tablaFaenas.getItems().addAll(lista);

        calcularAdeudos(lista);
    }

    private void calcularAdeudos(List<Faena> faenasDelAnio) {
        if (listaAdeudos == null || lblTotalAdeudo == null)
            return;

        listaAdeudos.getItems().clear();
        lblTotalAdeudo.setText("$ 0.00");

        if (ciudadanoSeleccionado == null || cbAnio == null || cbAnio.getValue() == null) {
            return;
        }

        // Vocal no debe nada
        String tipo = ciudadanoSeleccionado.getTipoCiudadano();
        double montoPorMes = calcularPagoReposicion(tipo);
        if (montoPorMes <= 0.0) {
            return;
        }

        LocalDate hoy = LocalDate.now();
        int anioSeleccionado = cbAnio.getValue();

        // Obtener fecha de registro (usar fecha de alta del ciudadano o fecha actual)
        LocalDate fechaRegistro = ciudadanoSeleccionado.getFechaAlta() != null 
                ? ciudadanoSeleccionado.getFechaAlta() 
                : hoy;

        // Calcular fecha cuando cumplió 18 años
        LocalDate nacimiento = ciudadanoSeleccionado.getFechaNacimiento();
        if (nacimiento == null) {
            return; // No podemos calcular sin fecha de nacimiento
        }

        LocalDate fechaCumple18 = nacimiento.plusYears(18);

        // Si el ciudadano aún no cumple 18 años, no tiene adeudos
        if (fechaCumple18.isAfter(fechaRegistro)) {
            return;
        }

        // Calcular todos los meses desde que cumplió 18 hasta la fecha de registro para el año seleccionado
        List<MesAnio> todosLosMesesAdeudo = calcularMesesAdeudoCompleto(fechaCumple18, fechaRegistro, anioSeleccionado);
        
        // Filtrar meses que ya están registrados (pagados o asistidos)
        Set<String> mesesConRegistro = faenasDelAnio.stream()
                .map(f -> normalizarMes(f.getMes()) + "_" + f.getAnio())
                .collect(Collectors.toSet());

        int adeudos = 0;
        for (MesAnio mesAnio : todosLosMesesAdeudo) {
            String claveMes = normalizarMes(mesAnio.nombreMes) + "_" + mesAnio.anio;
            if (!mesesConRegistro.contains(claveMes)) {
                listaAdeudos.getItems().add(mesAnio.nombreMes + " " + mesAnio.anio);
                adeudos++;
            }
        }

        double total = adeudos * montoPorMes;
        lblTotalAdeudo.setText(String.format("$ %.2f", total));
    }

    /**
     * Calcula todos los meses de adeudo desde que cumplió 18 años hasta la fecha de registro
     * para el año seleccionado en el combobox
     */
    private List<MesAnio> calcularMesesAdeudoCompleto(LocalDate fechaCumple18, LocalDate fechaRegistro, int anioSeleccionado) {
        List<MesAnio> mesesAdeudo = new ArrayList<>();
        
        // Empezar desde el mes siguiente a cumplir 18 años
        YearMonth inicio = YearMonth.from(fechaCumple18).plusMonths(1);
        // Hasta el mes anterior al registro (o mes actual si es anterior)
        YearMonth fin = YearMonth.from(fechaRegistro);
        
        YearMonth current = inicio;
        while (!current.isAfter(fin)) {
            // Solo incluir meses del año seleccionado
            if (current.getYear() == anioSeleccionado) {
                MesAnio mesAnio = new MesAnio();
                mesAnio.nombreMes = MESES.get(current.getMonthValue() - 1);
                mesAnio.anio = current.getYear();
                mesesAdeudo.add(mesAnio);
            }
            current = current.plusMonths(1);
        }
        
        return mesesAdeudo;
    }

    private String normalizarMes(String mes) {
        return mes == null ? "" : mes.trim().toLowerCase(Locale.ROOT);
    }

    private void limpiarAdeudos() {
        if (listaAdeudos != null)
            listaAdeudos.getItems().clear();
        if (lblTotalAdeudo != null)
            lblTotalAdeudo.setText("$ 0.00");
    }

    private double obtenerMontoPorMesActual() {
        if (ciudadanoSeleccionado == null)
            return 0.0;
        return calcularPagoReposicion(ciudadanoSeleccionado.getTipoCiudadano());
    }

    // ---------- PAGO DE ADEUDOS ----------

    private static class MesAnio {
        String nombreMes;
        int anio;
    }

    private MesAnio parseMesAnio(String texto) {
        if (texto == null || texto.isBlank())
            return null;
        String[] partes = texto.trim().split("\\s+");
        if (partes.length < 2)
            return null;
        String anioStr = partes[partes.length - 1];
        int anio;
        try {
            anio = Integer.parseInt(anioStr);
        } catch (NumberFormatException e) {
            return null;
        }
        String nombreMes = String.join(" ",
                Arrays.copyOf(partes, partes.length - 1));
        if (!MESES.contains(nombreMes))
            return null;

        MesAnio ma = new MesAnio();
        ma.nombreMes = nombreMes;
        ma.anio = anio;
        return ma;
    }

    private void pagarMeses(Collection<String> items) {
        if (ciudadanoSeleccionado == null) {
            mostrarAlerta("Validación", "Primero selecciona un ciudadano.", Alert.AlertType.WARNING);
            return;
        }
        if (items == null || items.isEmpty()) {
            mostrarAlerta("Validación", "No hay meses seleccionados para pagar.", Alert.AlertType.WARNING);
            return;
        }

        double montoPorMes = obtenerMontoPorMesActual();
        if (montoPorMes <= 0.0) {
            mostrarAlerta("Información", "Este tipo de ciudadano no genera adeudos (vocal u otro exento).",
                    Alert.AlertType.INFORMATION);
            return;
        }

        LocalDate fecha = (dpFechaRegistro != null && dpFechaRegistro.getValue() != null)
                ? dpFechaRegistro.getValue()
                : LocalDate.now();

        int exitos = 0;
        for (String item : items) {
            MesAnio ma = parseMesAnio(item);
            if (ma == null)
                continue;

            Faena faena = new Faena();
            faena.setAnio(ma.anio);
            faena.setMes(ma.nombreMes);
            faena.setAsistencia(false); // no fue, pero paga
            faena.setPagoReposicion(montoPorMes);
            faena.setFechaRegistro(fecha);
            faena.setIdCiudadano(ciudadanoSeleccionado.getIdCiudadano());

            if (faenaDAO.insertarFaena(faena)) {
                exitos++;
            }
        }

        if (exitos > 0) {
            mostrarAlerta("Éxito", "Se registraron " + exitos + " pagos de faenas.", Alert.AlertType.INFORMATION);
            cargarFaenas(); // recarga tabla y adeudos
        } else {
            mostrarAlerta("Error", "No se pudo registrar ningún pago.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void onPagarSeleccionados() {
        if (listaAdeudos == null)
            return;
        ObservableList<String> seleccion = listaAdeudos.getSelectionModel().getSelectedItems();
        pagarMeses(new ArrayList<>(seleccion)); // copia para evitar problemas si se limpia la lista
    }

    @FXML
    private void onPagarTodos() {
        if (listaAdeudos == null)
            return;
        pagarMeses(new ArrayList<>(listaAdeudos.getItems()));
    }

    // ---------- BOTONES ----------

    @FXML
    private void onGuardar() {
        if (!validarFormularioBasico()) {
            return;
        }

        if (cbAnio == null || cbAnio.getValue() == null) {
            mostrarAlerta("Validación", "Selecciona un año.", Alert.AlertType.WARNING);
            return;
        }
        if (cbMes == null || cbMes.getValue() == null) {
            mostrarAlerta("Validación", "Selecciona un mes.", Alert.AlertType.WARNING);
            return;
        }

        int anio = cbAnio.getValue();
        String mes = cbMes.getValue();

        boolean asistencia = chkAsistencia == null || chkAsistencia.isSelected();

        LocalDate fecha = (dpFechaRegistro != null && dpFechaRegistro.getValue() != null)
                ? dpFechaRegistro.getValue()
                : LocalDate.now();

        String tipo = ciudadanoSeleccionado != null ? ciudadanoSeleccionado.getTipoCiudadano() : null;
        double pago = asistencia ? 0.0 : calcularPagoReposicion(tipo);

        Faena faena = new Faena();
        faena.setAnio(anio);
        faena.setMes(mes);
        faena.setAsistencia(asistencia);
        faena.setPagoReposicion(pago);
        faena.setFechaRegistro(fecha);
        faena.setIdCiudadano(ciudadanoSeleccionado.getIdCiudadano());

        if (faenaDAO.insertarFaena(faena)) {
            mostrarAlerta("Éxito", "Faena registrada correctamente.", Alert.AlertType.INFORMATION);
            cargarFaenas();
            limpiarCamposFaena();
        } else {
            mostrarAlerta("Error", "No se pudo registrar la faena.", Alert.AlertType.ERROR);
        }
    }

    @FXML
private void onLimpiar() {
    if (txtIdCiudadano != null) {
        txtIdCiudadano.clear();
    }
    ciudadanoSeleccionado = null;

    if (lblNombreCiudadano != null)
        lblNombreCiudadano.setText("");
    if (lblTipoCiudadano != null)
        lblTipoCiudadano.setText("");
    if (tablaFaenas != null)
        tablaFaenas.getItems().clear();

    // Limpiar años cuando se limpia el formulario
    if (cbAnio != null)
        cbAnio.getItems().clear();
    
    int anioActual = LocalDate.now().getYear();
    if (cbMes != null)
        cbMes.getSelectionModel().select(LocalDate.now().getMonthValue() - 1);
    if (chkAsistencia != null)
        chkAsistencia.setSelected(true);
    if (dpFechaRegistro != null)
        dpFechaRegistro.setValue(LocalDate.now());

    limpiarAdeudos();
    actualizarPagoReposicion();
}

    @FXML
    private void onCancelar() {
        if (btnCancelar != null) {
            Stage stage = (Stage) btnCancelar.getScene().getWindow();
            stage.close();
        }
    }

    private void limpiarCamposFaena() {
        if (chkAsistencia != null)
            chkAsistencia.setSelected(true);
        if (dpFechaRegistro != null)
            dpFechaRegistro.setValue(LocalDate.now());
        actualizarPagoReposicion();
    }

    private boolean validarFormularioBasico() {
        if (ciudadanoSeleccionado == null) {
            mostrarAlerta("Validación", "Debe seleccionar un ciudadano válido.", Alert.AlertType.WARNING);
            if (txtIdCiudadano != null)
                txtIdCiudadano.requestFocus();
            return false;
        }
        return true;
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}