/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gestorcopaci.controllers;

import gestorcopaci.daos.CiudadanoDAO;
import gestorcopaci.models.Ciudadano;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class RegistroCiudadanosController {

    @FXML private TextField txtNombre;
    @FXML private DatePicker dpFechaNacimiento;
    @FXML private TextField txtLugarNacimiento;
    @FXML private ComboBox<String> cbGradoEstudios;
    @FXML private TextField txtDomicilio;
    @FXML private TextField txtManzana;
    @FXML private ComboBox<String> cbEstadoCivil;
    @FXML private TextField txtOcupacion;
    @FXML private ComboBox<String> cbTipoCiudadano;
    @FXML private DatePicker dpFechaAlta;
    @FXML private ComboBox<String> cbTipoCertificado;
    @FXML private Button btnGuardar;
    @FXML private Button btnLimpiar;
    @FXML private Button btnCancelar;

    private CiudadanoDAO ciudadanoDAO;

@FXML
private void initialize() {
    ciudadanoDAO = new CiudadanoDAO();
    configurarCombobox();
    configurarEventos();
    dpFechaAlta.setValue(LocalDate.now()); // Fecha actual por defecto
    
    // Opcional: Agregar listener para validar edad automáticamente
    dpFechaNacimiento.valueProperty().addListener((observable, oldValue, newValue) -> {
        if (newValue != null) {
            validarEdadSilenciosa(); // Validación silenciosa sin alertas
        }
    });
}

// Validación silenciosa que se ejecuta automáticamente al cambiar la fecha
private void validarEdadSilenciosa() {
    if (dpFechaNacimiento.getValue() == null) return;
    
    LocalDate fechaNacimiento = dpFechaNacimiento.getValue();
    LocalDate hoy = LocalDate.now();
    
    // Verificar que la fecha no sea en el futuro
    if (fechaNacimiento.isAfter(hoy)) {
        dpFechaNacimiento.setStyle("-fx-border-color: #f44336; -fx-border-width: 2px;");
        return;
    }
    
    // Calcular edad
    int edad = Period.between(fechaNacimiento, hoy).getYears();
    
    if (edad < 18) {
        dpFechaNacimiento.setStyle("-fx-border-color: #ff9800; -fx-border-width: 2px;");
    } else {
        dpFechaNacimiento.setStyle("-fx-border-color: #4caf50; -fx-border-width: 1px;");
    }
}

    private void configurarCombobox() {
    // Grados de estudios (se mantienen igual)
    cbGradoEstudios.getItems().addAll(
        "Sin estudios", "Primaria", "Secundaria", 
        "Preparatoria", "Técnico", "Licenciatura", 
        "Maestría", "Doctorado"
    );

    // Estados civiles (se mantienen igual)
    cbEstadoCivil.getItems().addAll(
        "Soltero(a)", "Casado(a)", "Divorciado(a)", 
        "Viudo(a)", "Unión Libre"
    );

    // Tipos de ciudadano - NUEVAS OPCIONES
    cbTipoCiudadano.getItems().addAll(
        "Estudiante", 
        "Madre Soltera", 
        "Común"
    );

    // Tipos de certificado - NUEVAS OPCIONES
    cbTipoCertificado.getItems().addAll(
        "Normal", 
        "Extemporáneo"
    );
}

    private void configurarEventos() {
        btnGuardar.setOnAction(event -> guardarCiudadano());
        btnLimpiar.setOnAction(event -> limpiarCampos());
        btnCancelar.setOnAction(event -> cerrarVentana());
    }

    private void guardarCiudadano() {
    if (!validarCampos()) {
        return;
    }

    try {
        Ciudadano ciudadano = new Ciudadano(
            txtNombre.getText().trim(),
            dpFechaNacimiento.getValue(),
            txtDomicilio.getText().trim(),
            txtManzana.getText().trim(),
            cbEstadoCivil.getValue(),
            txtOcupacion.getText().trim(),
            cbTipoCiudadano.getValue(),
            dpFechaAlta.getValue(),
            cbTipoCertificado.getValue(),
            txtLugarNacimiento.getText().trim(),
            cbGradoEstudios.getValue()
        );

        System.out.println("Intentando guardar ciudadano: " + ciudadano.getNombre());
        
        if (ciudadanoDAO.insertarCiudadano(ciudadano)) {
            mostrarAlerta("Éxito", "Ciudadano registrado correctamente en la base de datos", Alert.AlertType.INFORMATION);
            limpiarCampos();
        } else {
            mostrarAlerta("Error", "No se pudo registrar el ciudadano en la base de datos", Alert.AlertType.ERROR);
        }

    } catch (Exception e) {
        System.err.println("Error completo: " + e.getMessage());
        e.printStackTrace();
        mostrarAlerta("Error", "Error al registrar ciudadano: " + e.getMessage(), Alert.AlertType.ERROR);
    }
}

    private boolean validarEdad() {
    if (dpFechaNacimiento.getValue() == null) {
        return true; // No validar si no hay fecha seleccionada
    }
    
    LocalDate fechaNacimiento = dpFechaNacimiento.getValue();
    LocalDate hoy = LocalDate.now();
    
    // Verificar que la fecha no sea en el futuro
    if (fechaNacimiento.isAfter(hoy)) {
        mostrarAlerta("Fecha Inválida", 
                     "La fecha de nacimiento no puede ser futura.",
                     Alert.AlertType.WARNING);
        dpFechaNacimiento.requestFocus();
        return false;
    }
    
    // Calcular edad exacta
    Period periodo = Period.between(fechaNacimiento, hoy);
    int años = periodo.getYears();
    int meses = periodo.getMonths();
    int dias = periodo.getDays();
    
    if (años < 18) {
        String mensajeEdad = String.format(
            "No se puede registrar: El ciudadano es menor de edad.\n\n" +
            "Detalles:\n" +
            "• Edad: %d años, %d meses, %d días\n" +
            "• Fecha de nacimiento: %s\n" +
            "• Se requiere ser mayor de 18 años para el registro.",
            años, meses, dias, 
            fechaNacimiento.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        );
        
        mostrarAlerta("Validación de Edad", mensajeEdad, Alert.AlertType.WARNING);
        dpFechaNacimiento.requestFocus();
        return false;
    }
    
    // Opcional: Mostrar confirmación para mayores de 100 años
    if (años >= 100) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmación de Edad");
        alert.setHeaderText("Edad avanzada detectada");
        alert.setContentText(String.format(
            "La edad calculada es de %d años. ¿Está seguro que la fecha de nacimiento es correcta?",
            años
        ));
        
        Optional<ButtonType> resultado = alert.showAndWait();
        if (resultado.isPresent() && resultado.get() != ButtonType.OK) {
            dpFechaNacimiento.requestFocus();
            return false;
        }
    }
    
    return true;
}
    
    private boolean validarCampos() {
    // Validación de campos obligatorios
    if (txtNombre.getText().trim().isEmpty()) {
        mostrarAlerta("Validación", "El nombre es obligatorio", Alert.AlertType.WARNING);
        txtNombre.requestFocus();
        return false;
    }

    if (dpFechaNacimiento.getValue() == null) {
        mostrarAlerta("Validación", "La fecha de nacimiento es obligatoria", Alert.AlertType.WARNING);
        dpFechaNacimiento.requestFocus();
        return false;
    }

    // Validar edad (mayor de 18 años)
    if (!validarEdad()) {
        return false; // Ya se mostró el mensaje en validarEdad()
    }

    if (txtDomicilio.getText().trim().isEmpty()) {
        mostrarAlerta("Validación", "El domicilio es obligatorio", Alert.AlertType.WARNING);
        txtDomicilio.requestFocus();
        return false;
    }

    if (cbEstadoCivil.getValue() == null) {
        mostrarAlerta("Validación", "El estado civil es obligatorio", Alert.AlertType.WARNING);
        cbEstadoCivil.requestFocus();
        return false;
    }

    return true;
}

    private void limpiarCampos() {
        txtNombre.clear();
        dpFechaNacimiento.setValue(null);
        txtLugarNacimiento.clear();
        cbGradoEstudios.setValue(null);
        txtDomicilio.clear();
        txtManzana.clear();
        cbEstadoCivil.setValue(null);
        txtOcupacion.clear();
        cbTipoCiudadano.setValue(null);
        dpFechaAlta.setValue(LocalDate.now());
        cbTipoCertificado.setValue(null);
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