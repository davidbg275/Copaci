package gestorcopaci.controllers;

import gestorcopaci.daos.CooperacionDAO;
import gestorcopaci.daos.CiudadanoDAO;
import gestorcopaci.models.Cooperacion;
import gestorcopaci.models.Ciudadano;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;

public class CooperacionesController {

    @FXML private TextField txtIdCiudadano;
    @FXML private TextField txtAnio;
    @FXML private CheckBox cbBanda;
    @FXML private CheckBox cbCastillo;
    @FXML private CheckBox cbPaseo;
    @FXML private TextField txtCooperacionExtra;
    @FXML private DatePicker dpFechaRegistro;
    @FXML private Label lblDescuento;
    @FXML private Label lblTotalPagar;
    @FXML private ComboBox<String> cbTipoPago;
    @FXML private Button btnGuardar;
    @FXML private Button btnLimpiar;
    @FXML private Button btnCancelar;
    @FXML private Label lblNombreCiudadano;
    @FXML private Label lblTipoCiudadano;
    @FXML private Label lblResumenCooperaciones;

    private CooperacionDAO cooperacionDAO;
    private CiudadanoDAO ciudadanoDAO;

    @FXML
    private void initialize() {
        cooperacionDAO = new CooperacionDAO();
        ciudadanoDAO = new CiudadanoDAO();
        configurarCombobox();
        configurarEventos();
        dpFechaRegistro.setValue(LocalDate.now());
        txtAnio.setText(String.valueOf(Year.now().getValue()));
        
        // Listener para buscar ciudadano automáticamente
        txtIdCiudadano.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                buscarCiudadano();
            } else {
                limpiarInfoCiudadano();
            }
        });
        
        // Listeners para recalcular total
        cbBanda.selectedProperty().addListener((observable, oldValue, newValue) -> calcularTotal());
        cbCastillo.selectedProperty().addListener((observable, oldValue, newValue) -> calcularTotal());
        cbPaseo.selectedProperty().addListener((observable, oldValue, newValue) -> calcularTotal());
        txtCooperacionExtra.textProperty().addListener((observable, oldValue, newValue) -> calcularTotal());
        
        // Listener para año
        txtAnio.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!txtIdCiudadano.getText().isEmpty() && !newValue.isEmpty()) {
                actualizarResumenCooperaciones();
            }
        });
    }

    private void configurarCombobox() {
        cbTipoPago.getItems().addAll("Efectivo", "Transferencia", "Tarjeta");
    }

    private void configurarEventos() {
        btnGuardar.setOnAction(event -> guardarCooperacion());
        btnLimpiar.setOnAction(event -> limpiarCampos());
        btnCancelar.setOnAction(event -> cerrarVentana());
    }

    private void buscarCiudadano() {
        String idText = txtIdCiudadano.getText().trim();
        if (idText.isEmpty()) {
            limpiarInfoCiudadano();
            return;
        }

        try {
            int idCiudadano = Integer.parseInt(idText);
            List<Ciudadano> ciudadanos = ciudadanoDAO.obtenerTodosCiudadanos();
            
            for (Ciudadano ciudadano : ciudadanos) {
                if (ciudadano.getIdCiudadano() == idCiudadano) {
                    mostrarInfoCiudadano(ciudadano);
                    actualizarResumenCooperaciones();
                    calcularTotal();
                    return;
                }
            }
            
            lblNombreCiudadano.setText("❌ Ciudadano no encontrado");
            lblNombreCiudadano.setStyle("-fx-text-fill: #f44336; -fx-font-weight: bold;");
            lblTipoCiudadano.setText("");
            lblResumenCooperaciones.setText("");
            
        } catch (NumberFormatException e) {
            lblNombreCiudadano.setText("❌ ID inválido");
            lblNombreCiudadano.setStyle("-fx-text-fill: #f44336; -fx-font-weight: bold;");
            lblTipoCiudadano.setText("");
            lblResumenCooperaciones.setText("");
        }
    }

    private void mostrarInfoCiudadano(Ciudadano ciudadano) {
        lblNombreCiudadano.setText("👤 " + ciudadano.getNombre());
        lblNombreCiudadano.setStyle("-fx-text-fill: #4caf50; -fx-font-weight: bold;");
        
        String tipo = ciudadano.getTipoCiudadano();
        lblTipoCiudadano.setText("🎯 Tipo: " + tipo);
        
        if ("Estudiante".equals(tipo) || "Madre Soltera".equals(tipo)) {
            lblTipoCiudadano.setStyle("-fx-text-fill: #ff9800; -fx-font-weight: bold;");
            lblDescuento.setText("💰 Descuento aplicado: 50%");
        } else {
            lblTipoCiudadano.setStyle("-fx-text-fill: #2196f3; -fx-font-weight: bold;");
            lblDescuento.setText("💰 Sin descuento");
        }
    }

    private void actualizarResumenCooperaciones() {
        try {
            int idCiudadano = Integer.parseInt(txtIdCiudadano.getText().trim());
            int anio = Integer.parseInt(txtAnio.getText().trim());
            
            String resumen = cooperacionDAO.obtenerResumenCooperaciones(idCiudadano, anio);
            lblResumenCooperaciones.setText("📊 " + resumen);
            lblResumenCooperaciones.setStyle("-fx-text-fill: #1976d2; -fx-font-weight: bold;");
            
        } catch (NumberFormatException e) {
            lblResumenCooperaciones.setText("");
        }
    }

    private void limpiarInfoCiudadano() {
        lblNombreCiudadano.setText("");
        lblTipoCiudadano.setText("");
        lblDescuento.setText("");
        lblTotalPagar.setText("Total a pagar: $0.00");
        lblResumenCooperaciones.setText("");
    }

    private void calcularTotal() {
    String idText = txtIdCiudadano.getText().trim();
    if (idText.isEmpty()) {
        lblTotalPagar.setText("Total a pagar: $0.00");
        return;
    }

    try {
        int idCiudadano = Integer.parseInt(idText);
        String tipoCiudadano = cooperacionDAO.obtenerTipoCiudadano(idCiudadano);
        
        int extra = 0; // ← Cambiado a int
        try {
            extra = Integer.parseInt(txtCooperacionExtra.getText().trim()); // ← Cambiado a parseInt
        } catch (NumberFormatException e) {
            // Si no es un número válido, se considera 0
        }
        
        double total = cooperacionDAO.calcularTotalConDescuento(
            tipoCiudadano,
            cbBanda.isSelected(),
            cbCastillo.isSelected(),
            cbPaseo.isSelected(),
            extra // ← Se convierte automáticamente a double
        );
        
        lblTotalPagar.setText(String.format("💵 Total a pagar: $%,.2f", total));
        
    } catch (NumberFormatException e) {
        lblTotalPagar.setText("Total a pagar: $0.00");
    }
}

    private void guardarCooperacion() {
    if (!validarCampos()) {
        return;
    }

    try {
        int idCiudadano = Integer.parseInt(txtIdCiudadano.getText().trim());
        int anio = Integer.parseInt(txtAnio.getText().trim());
        
        // Verificar límites de cooperaciones
        String errorLimites = cooperacionDAO.verificarLimitesCooperacion(
            idCiudadano, anio, 
            cbBanda.isSelected(), 
            cbCastillo.isSelected(), 
            cbPaseo.isSelected()
        );
        
        if (errorLimites != null) {
            mostrarAlerta("Límite Alcanzado", errorLimites, Alert.AlertType.WARNING);
            return;
        }
        
        String tipoCiudadano = cooperacionDAO.obtenerTipoCiudadano(idCiudadano);
        
        int extra = 0; // ← Cambiado a int
        if (!txtCooperacionExtra.getText().trim().isEmpty()) {
            extra = Integer.parseInt(txtCooperacionExtra.getText().trim()); // ← Cambiado a parseInt
        }
        
        double total = cooperacionDAO.calcularTotalConDescuento(
            tipoCiudadano,
            cbBanda.isSelected(),
            cbCastillo.isSelected(),
            cbPaseo.isSelected(),
            extra // ← Ya es int, pero el método espera double (funciona por conversión automática)
        );
        
        Cooperacion cooperacion = new Cooperacion(
            anio,
            cbBanda.isSelected() ? 400.0 : 0.0,
            cbCastillo.isSelected() ? 400.0 : 0.0,
            cbPaseo.isSelected() ? 200.0 : 0.0,
            extra, // ← Cambiado a int
            dpFechaRegistro.getValue(),
            ("Estudiante".equals(tipoCiudadano) || "Madre Soltera".equals(tipoCiudadano)) ? "50%" : "0%",
            total,
            cbTipoPago.getValue(),
            idCiudadano
        );

        if (cooperacionDAO.insertarCooperacion(cooperacion)) {
            mostrarAlerta("✅ Éxito", 
                "Cooperación registrada correctamente\n\n" +
                "📋 Detalles:\n" +
                "• Total pagado: $" + String.format("%,.2f", total) + "\n" +
                "• Año: " + anio + "\n" +
                "• Tipo de pago: " + cbTipoPago.getValue(),
                Alert.AlertType.INFORMATION);
            limpiarCampos();
            actualizarResumenCooperaciones();
        } else {
            mostrarAlerta("❌ Error", "No se pudo registrar la cooperación", Alert.AlertType.ERROR);
        }

    } catch (NumberFormatException e) {
        mostrarAlerta("❌ Error", "El monto de cooperación extra debe ser un número entero válido", Alert.AlertType.ERROR);
    } catch (Exception e) {
        mostrarAlerta("❌ Error", "Error al registrar cooperación: " + e.getMessage(), Alert.AlertType.ERROR);
    }
}

    private boolean validarCampos() {
        // Validar ID Ciudadano
        if (txtIdCiudadano.getText().trim().isEmpty()) {
            mostrarAlerta("Validación", "El ID del ciudadano es obligatorio", Alert.AlertType.WARNING);
            txtIdCiudadano.requestFocus();
            return false;
        }

        // Validar que el ciudadano exista
        try {
            int id = Integer.parseInt(txtIdCiudadano.getText().trim());
            String tipo = cooperacionDAO.obtenerTipoCiudadano(id);
            if (tipo == null) {
                mostrarAlerta("Validación", "El ciudadano no existe", Alert.AlertType.WARNING);
                txtIdCiudadano.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("Validación", "El ID del ciudadano debe ser un número válido", Alert.AlertType.WARNING);
            txtIdCiudadano.requestFocus();
            return false;
        }

        // Validar Año
        if (txtAnio.getText().trim().isEmpty()) {
            mostrarAlerta("Validación", "El año es obligatorio", Alert.AlertType.WARNING);
            txtAnio.requestFocus();
            return false;
        }

        try {
            int anio = Integer.parseInt(txtAnio.getText().trim());
            int anioActual = Year.now().getValue();
            if (anio < 2000 || anio > anioActual + 1) {
                mostrarAlerta("Validación", "El año debe ser entre 2000 y " + (anioActual + 1), Alert.AlertType.WARNING);
                txtAnio.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("Validación", "El año debe ser un número válido", Alert.AlertType.WARNING);
            txtAnio.requestFocus();
            return false;
        }

        // Validar que se seleccione al menos una cooperación
        if (!cbBanda.isSelected() && !cbCastillo.isSelected() && !cbPaseo.isSelected() && txtCooperacionExtra.getText().trim().isEmpty()) {
            mostrarAlerta("Validación", "Debe seleccionar al menos una cooperación o ingresar cooperación extra", Alert.AlertType.WARNING);
            cbBanda.requestFocus();
            return false;
        }

        // Validar Tipo de Pago
        if (cbTipoPago.getValue() == null) {
            mostrarAlerta("Validación", "El tipo de pago es obligatorio", Alert.AlertType.WARNING);
            cbTipoPago.requestFocus();
            return false;
        }

    if (!txtCooperacionExtra.getText().trim().isEmpty()) {
    try {
        int extra = Integer.parseInt(txtCooperacionExtra.getText().trim());
        if (extra < 0) {
            mostrarAlerta("Validación", "La cooperación extra no puede ser negativa", Alert.AlertType.WARNING);
            txtCooperacionExtra.requestFocus();
            return false;
        }
    } catch (NumberFormatException e) {
        mostrarAlerta("Validación", "La cooperación extra debe ser un número entero válido", Alert.AlertType.WARNING);
        txtCooperacionExtra.requestFocus();
        return false;
    }
}

        return true;
    }

    private void limpiarCampos() {
        // No limpiar ID y Año para facilitar registro múltiple
        cbBanda.setSelected(false);
        cbCastillo.setSelected(false);
        cbPaseo.setSelected(false);
        txtCooperacionExtra.clear();
        dpFechaRegistro.setValue(LocalDate.now());
        cbTipoPago.setValue(null);
        calcularTotal();
        actualizarResumenCooperaciones();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}