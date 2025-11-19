package gestorcopaci.controllers;

import java.time.LocalDate;
import java.util.List;

import gestorcopaci.daos.CiudadanoDAO;
import gestorcopaci.daos.FaenaDAO;
import gestorcopaci.models.Ciudadano;
import gestorcopaci.models.Faena;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
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
    private Button btnGuardar;
    @FXML
    private Button btnLimpiar;
    @FXML
    private Button btnCancelar;

    // ---------- CAMPOS LÓGICOS ----------

    private FaenaDAO faenaDAO;
    private CiudadanoDAO ciudadanoDAO;
    private Ciudadano ciudadanoSeleccionado;

    // ---------- INITIALIZE ----------

    @FXML
    private void initialize() {
        faenaDAO = new FaenaDAO();
        ciudadanoDAO = new CiudadanoDAO();

        int anioActual = LocalDate.now().getYear();

        // Años
        if (cbAnio != null) {
            for (int a = anioActual - 5; a <= anioActual + 1; a++) {
                cbAnio.getItems().add(a);
            }
            cbAnio.getSelectionModel().select(Integer.valueOf(anioActual));
            cbAnio.valueProperty().addListener((obs, o, n) -> cargarFaenas());
        }

        // Meses
        if (cbMes != null) {
            cbMes.getItems().addAll(
                    "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                    "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
            );
            cbMes.getSelectionModel().select(LocalDate.now().getMonthValue() - 1);
        }

        // Fecha
        if (dpFechaRegistro != null) {
            dpFechaRegistro.setValue(LocalDate.now());
        }

        // Tabla
        if (tablaFaenas != null) {
            colAnio.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getAnio()));
            colMes.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getMes()));
            colAsistencia.setCellValueFactory(c ->
                    new SimpleStringProperty(c.getValue().isAsistencia() ? "Sí" : "No"));
            colPago.setCellValueFactory(c ->
                    new SimpleStringProperty(String.format("$ %.2f", c.getValue().getPagoReposicion())));
            colFecha.setCellValueFactory(c ->
                    new SimpleStringProperty(
                            c.getValue().getFechaRegistro() != null
                                    ? c.getValue().getFechaRegistro().toString()
                                    : ""
                    ));
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
        if (txtIdCiudadano == null) {
            return;
        }

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
     *  - Vocal: 100% desc → 0
     *  - Estudiante / Madre Soltera: 50% desc → 50
     *  - Normal: 100
     */
    private double calcularPagoReposicion(String tipoCiudadano) {
        final double BASE = 100.0;

        if (tipoCiudadano == null || tipoCiudadano.isEmpty()) {
            return BASE;
        }

        String tipo = tipoCiudadano.toLowerCase();

        if (tipo.contains("vocal")) {
            return 0.0;
        }
        if (tipo.contains("estudiante") || tipo.contains("madre")) {
            return BASE * 0.5;
        }
        return BASE;
    }

    private void cargarFaenas() {
        if (tablaFaenas == null || ciudadanoSeleccionado == null || cbAnio == null) {
            return;
        }

        tablaFaenas.getItems().clear();

        Integer anio = cbAnio.getValue();
        if (anio == null) {
            return;
        }

        List<Faena> lista = faenaDAO.obtenerFaenasPorCiudadanoYAnio(
                ciudadanoSeleccionado.getIdCiudadano(),
                anio
        );
        tablaFaenas.getItems().addAll(lista);
    }

    // ---------- BOTONES ----------

    @FXML
    private void onGuardar() {
        if (!validarFormularioBasico()) {
            return;
        }

        int anio = (cbAnio != null && cbAnio.getValue() != null)
                ? cbAnio.getValue()
                : LocalDate.now().getYear();

        String mes = (cbMes != null && cbMes.getValue() != null)
                ? cbMes.getValue()
                : "Sin mes";

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

        if (lblNombreCiudadano != null) lblNombreCiudadano.setText("");
        if (lblTipoCiudadano != null) lblTipoCiudadano.setText("");
        if (tablaFaenas != null) tablaFaenas.getItems().clear();

        int anioActual = LocalDate.now().getYear();
        if (cbAnio != null) cbAnio.getSelectionModel().select(Integer.valueOf(anioActual));
        if (cbMes != null) cbMes.getSelectionModel().select(LocalDate.now().getMonthValue() - 1);
        if (chkAsistencia != null) chkAsistencia.setSelected(true);
        if (dpFechaRegistro != null) dpFechaRegistro.setValue(LocalDate.now());

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
        if (chkAsistencia != null) chkAsistencia.setSelected(true);
        if (dpFechaRegistro != null) dpFechaRegistro.setValue(LocalDate.now());
        actualizarPagoReposicion();
    }

    private boolean validarFormularioBasico() {
        if (ciudadanoSeleccionado == null) {
            mostrarAlerta("Validación", "Debe seleccionar un ciudadano válido.", Alert.AlertType.WARNING);
            if (txtIdCiudadano != null) txtIdCiudadano.requestFocus();
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
