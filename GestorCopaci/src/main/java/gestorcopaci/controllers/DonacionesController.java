/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gestorcopaci.controllers;

import gestorcopaci.daos.DonacionDAO;
import gestorcopaci.daos.CiudadanoDAO;
import gestorcopaci.models.Donacion;
import gestorcopaci.models.Ciudadano;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;

public class DonacionesController {

    @FXML
    private TextField txtIdCiudadano;
    @FXML
    private TextField txtConcepto;
    @FXML
    private TextField txtMonto;
    @FXML
    private DatePicker dpFechaDonacion;
    @FXML
    private Button btnGuardar;
    @FXML
    private Button btnLimpiar;
    @FXML
    private Button btnCancelar;
    @FXML
    private Label lblNombreCiudadano;
    @FXML
    private Label lblTotalDonaciones;

    private DonacionDAO donacionDAO;
    private CiudadanoDAO ciudadanoDAO;

    @FXML
    private void initialize() {
        donacionDAO = new DonacionDAO();
        ciudadanoDAO = new CiudadanoDAO();
        configurarEventos();
        dpFechaDonacion.setValue(LocalDate.now());
        actualizarTotalDonaciones();

        // Listener para buscar ciudadano automáticamente
        txtIdCiudadano.textProperty().addListener((observable, oldValue, newValue) -> {
            buscarCiudadano();
        });
    }

    private void configurarEventos() {
        btnGuardar.setOnAction(event -> guardarDonacion());
        btnLimpiar.setOnAction(event -> limpiarCampos());
        btnCancelar.setOnAction(event -> cerrarVentana());
    }

    private void buscarCiudadano() {
        String idText = txtIdCiudadano.getText().trim();
        if (idText.isEmpty()) {
            lblNombreCiudadano.setText("");
            return;
        }

        try {
            int idCiudadano = Integer.parseInt(idText);
            List<Ciudadano> ciudadanos = ciudadanoDAO.obtenerTodosCiudadanos();

            for (Ciudadano ciudadano : ciudadanos) {
                if (ciudadano.getIdCiudadano() == idCiudadano) {
                    lblNombreCiudadano.setText("Ciudadano: " + ciudadano.getNombre());
                    lblNombreCiudadano.setStyle("-fx-text-fill: #4caf50; -fx-font-weight: bold;");
                    return;
                }
            }

            lblNombreCiudadano.setText("Ciudadano no encontrado");
            lblNombreCiudadano.setStyle("-fx-text-fill: #f44336; -fx-font-weight: bold;");

        } catch (NumberFormatException e) {
            lblNombreCiudadano.setText("ID inválido");
            lblNombreCiudadano.setStyle("-fx-text-fill: #f44336; -fx-font-weight: bold;");
        }
    }

    private void guardarDonacion() {
        if (!validarCampos()) {
            return;
        }

        try {
            Donacion donacion = new Donacion(
                    Integer.parseInt(txtIdCiudadano.getText().trim()),
                    txtConcepto.getText().trim(),
                    Double.parseDouble(txtMonto.getText().trim()),
                    dpFechaDonacion.getValue());

            if (donacionDAO.insertarDonacion(donacion)) {
                mostrarAlerta("Éxito", "Donación registrada correctamente", Alert.AlertType.INFORMATION);
                limpiarCampos();
                actualizarTotalDonaciones();
            } else {
                mostrarAlerta("Error", "No se pudo registrar la donación", Alert.AlertType.ERROR);
            }

        } catch (Exception e) {
            mostrarAlerta("Error", "Error al registrar donación: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private boolean validarCampos() {
        // Validar ID Ciudadano
        if (txtIdCiudadano.getText().trim().isEmpty()) {
            mostrarAlerta("Validación", "El ID del ciudadano es obligatorio", Alert.AlertType.WARNING);
            txtIdCiudadano.requestFocus();
            return false;
        }

        try {
            int id = Integer.parseInt(txtIdCiudadano.getText().trim());
            if (id <= 0) {
                mostrarAlerta("Validación", "El ID del ciudadano debe ser mayor a 0", Alert.AlertType.WARNING);
                txtIdCiudadano.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("Validación", "El ID del ciudadano debe ser un número válido", Alert.AlertType.WARNING);
            txtIdCiudadano.requestFocus();
            return false;
        }

        // Validar Concepto
        if (txtConcepto.getText().trim().isEmpty()) {
            mostrarAlerta("Validación", "El concepto es obligatorio", Alert.AlertType.WARNING);
            txtConcepto.requestFocus();
            return false;
        }

        // Validar Monto
        if (txtMonto.getText().trim().isEmpty()) {
            mostrarAlerta("Validación", "El monto es obligatorio", Alert.AlertType.WARNING);
            txtMonto.requestFocus();
            return false;
        }

        try {
            double monto = Double.parseDouble(txtMonto.getText().trim());
            if (monto <= 0) {
                mostrarAlerta("Validación", "El monto debe ser mayor a 0", Alert.AlertType.WARNING);
                txtMonto.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("Validación", "El monto debe ser un número válido", Alert.AlertType.WARNING);
            txtMonto.requestFocus();
            return false;
        }

        // Validar Fecha
        if (dpFechaDonacion.getValue() == null) {
            mostrarAlerta("Validación", "La fecha de donación es obligatoria", Alert.AlertType.WARNING);
            dpFechaDonacion.requestFocus();
            return false;
        }

        if (dpFechaDonacion.getValue().isAfter(LocalDate.now())) {
            mostrarAlerta("Validación", "La fecha de donación no puede ser futura", Alert.AlertType.WARNING);
            dpFechaDonacion.requestFocus();
            return false;
        }

        return true;
    }

    private void limpiarCampos() {
        txtIdCiudadano.clear();
        txtConcepto.clear();
        txtMonto.clear();
        dpFechaDonacion.setValue(LocalDate.now());
        lblNombreCiudadano.setText("");
    }

    private void actualizarTotalDonaciones() {
        double total = donacionDAO.obtenerTotalDonaciones();
        lblTotalDonaciones.setText(String.format("Total Donaciones: $%,.2f", total));
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