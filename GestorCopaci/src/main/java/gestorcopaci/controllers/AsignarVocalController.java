package gestorcopaci.controllers;

import gestorcopaci.daos.VocalDAO;
import gestorcopaci.models.Vocal;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class AsignarVocalController implements Initializable {

    @FXML private TextField txtIdCiudadano;
    @FXML private Button btnAsignar;
    @FXML private Button btnCancelar;
    @FXML private Label lblInfoCiudadano;
    @FXML private VBox panelInfoCiudadano;

    private VocalDAO vocalDAO;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        vocalDAO = new VocalDAO();
        
        // Ocultar el panel de información inicialmente
        panelInfoCiudadano.setVisible(false);
        
        // Configurar el TextField para solo aceptar números
        txtIdCiudadano.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                txtIdCiudadano.setText(newValue.replaceAll("[^\\d]", ""));
            }
            
            // Cuando el texto cambie, buscar el ciudadano
            if (!newValue.isEmpty()) {
                buscarCiudadano();
            } else {
                // Si está vacío, ocultar el panel
                panelInfoCiudadano.setVisible(false);
            }
        });
    }
    
    private void buscarCiudadano() {
        String idTexto = txtIdCiudadano.getText().trim();
        
        if (idTexto.isEmpty()) {
            panelInfoCiudadano.setVisible(false);
            return;
        }
        
        try {
            int idCiudadano = Integer.parseInt(idTexto);
            
            // Buscar información del ciudadano
            String nombreCiudadano = vocalDAO.obtenerNombreCiudadano(idCiudadano);
            
            if (nombreCiudadano.startsWith("Ciudadano ID: ")) {
                // No se encontró el ciudadano
                lblInfoCiudadano.setText("❌ Ciudadano no encontrado");
                lblInfoCiudadano.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                panelInfoCiudadano.setVisible(true);
            } else {
                // Ciudadano encontrado
                lblInfoCiudadano.setText("✅ " + nombreCiudadano);
                lblInfoCiudadano.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                panelInfoCiudadano.setVisible(true);
            }
            
        } catch (NumberFormatException e) {
            lblInfoCiudadano.setText("❌ ID inválido");
            lblInfoCiudadano.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
            panelInfoCiudadano.setVisible(true);
        }
    }
    
    @FXML
private void handleAsignar() {
    String idTexto = txtIdCiudadano.getText().trim();
    
    // Validar que no esté vacío
    if (idTexto.isEmpty()) {
        mostrarAlerta("Error", "Por favor ingrese el ID del ciudadano", Alert.AlertType.ERROR);
        return;
    }
    
    try {
        int idCiudadano = Integer.parseInt(idTexto);
        
        if (idCiudadano <= 0) {
            mostrarAlerta("Error", "El ID de ciudadano debe ser un número positivo", Alert.AlertType.ERROR);
            return;
        }
        
        // Verificar si el ciudadano existe
        String nombreCiudadano = vocalDAO.obtenerNombreCiudadano(idCiudadano);
        if (nombreCiudadano.startsWith("Ciudadano ID: ")) {
            mostrarAlerta("Error", "El ciudadano con ID " + idCiudadano + " no existe", Alert.AlertType.ERROR);
            return;
        }
        
        // Verificar si el ciudadano ya es vocal - MÁS ROBUSTO
        Vocal vocalExistente = vocalDAO.obtenerVocalPorCiudadano(idCiudadano);
        if (vocalExistente != null) {
            String estado = vocalExistente.getEstado() != null ? vocalExistente.getEstado() : "desconocido";
            mostrarAlerta("Error", 
                "Este ciudadano ya está asignado como vocal\n\n" +
                "ID Vocal: " + vocalExistente.getIdVocal() + "\n" +
                "Estado: " + estado + "\n" +
                "Eventos asistidos: " + vocalExistente.getEventosAsistidos() + "/10",
                Alert.AlertType.ERROR);
            return;
        }
        
        // Mostrar confirmación
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Asignación");
        confirmacion.setHeaderText("¿Está seguro de asignar este ciudadano como vocal?");
        confirmacion.setContentText("Ciudadano: " + nombreCiudadano + "\nID: " + idCiudadano + "\n\nEl vocal tendrá un mandato de 1 año.");
        
        if (confirmacion.showAndWait().get() == javafx.scene.control.ButtonType.OK) {
            // Asignar nuevo vocal
            boolean exito = vocalDAO.asignarVocal(idCiudadano);
            
            if (exito) {
                mostrarAlerta("Éxito", 
                    "Vocal asignado correctamente\n" +
                    "Ciudadano: " + nombreCiudadano + "\n" +
                    "ID: " + idCiudadano + "\n" +
                    "Fecha de inicio: " + java.time.LocalDate.now() + "\n" +
                    "Fecha de fin: " + java.time.LocalDate.now().plusYears(1), 
                    Alert.AlertType.INFORMATION);
                cerrarVentana();
            } else {
                mostrarAlerta("Error", 
                    "No se pudo asignar el vocal. Posibles causas:\n" +
                    "• Error de conexión con la base de datos\n" +
                    "• El ciudadano ya podría estar asignado", 
                    Alert.AlertType.ERROR);
            }
        }
        
    } catch (NumberFormatException e) {
        mostrarAlerta("Error", "Por favor ingrese un ID de ciudadano válido (solo números)", Alert.AlertType.ERROR);
    } catch (Exception e) {
        mostrarAlerta("Error", "Error inesperado: " + e.getMessage(), Alert.AlertType.ERROR);
        e.printStackTrace();
    }
}
    
    @FXML
    private void handleCancelar() {
        cerrarVentana();
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