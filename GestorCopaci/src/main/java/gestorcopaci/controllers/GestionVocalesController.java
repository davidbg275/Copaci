package gestorcopaci.controllers;

import gestorcopaci.daos.VocalDAO;
import gestorcopaci.models.Vocal;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class GestionVocalesController implements Initializable {

    @FXML private TableView<Vocal> tablaVocales;
    @FXML private TableColumn<Vocal, Integer> colIdVocal;
    @FXML private TableColumn<Vocal, String> colNombre;
    @FXML private TableColumn<Vocal, String> colFechaInicio;
    @FXML private TableColumn<Vocal, String> colFechaFin;
    @FXML private TableColumn<Vocal, Integer> colEventosAsistidos;
    @FXML private TableColumn<Vocal, Double> colPorcentaje;
    @FXML private TableColumn<Vocal, String> colEstado;
    
    @FXML private Button btnAsignarVocal;
    @FXML private Button btnRegistrarAsistencia;
    @FXML private Button btnVerDetalles;
    @FXML private Button btnActualizar;

    private VocalDAO vocalDAO;
    private ObservableList<Vocal> vocalesList;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        vocalDAO = new VocalDAO();
        vocalesList = FXCollections.observableArrayList();
        
        configurarTabla();
        cargarVocales();
    }
    
    private void configurarTabla() {
        colIdVocal.setCellValueFactory(new PropertyValueFactory<>("idVocal"));
        
        // MOSTRAR EL NOMBRE REAL DEL CIUDADANO
        colNombre.setCellValueFactory(cellData -> {
            int idCiudadano = cellData.getValue().getIdCiudadano();
            String nombreCompleto = vocalDAO.obtenerNombreCiudadano(idCiudadano);
            return new javafx.beans.property.SimpleStringProperty(nombreCompleto);
        });
        
        colFechaInicio.setCellValueFactory(new PropertyValueFactory<>("fechaInicioMandato"));
        colFechaFin.setCellValueFactory(new PropertyValueFactory<>("fechaFinMandato"));
        colEventosAsistidos.setCellValueFactory(new PropertyValueFactory<>("eventosAsistidos"));
        colPorcentaje.setCellValueFactory(new PropertyValueFactory<>("porcentajeAsistencia"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        
        // Formatear el porcentaje para mostrar 2 decimales
        colPorcentaje.setCellFactory(column -> new TableCell<Vocal, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f%%", item));
                }
            }
        });
        
        tablaVocales.setItems(vocalesList);
    }
    
    private void cargarVocales() {
        vocalesList.clear();
        vocalesList.addAll(vocalDAO.obtenerTodosVocales());
    }
    
    @FXML
    private void handleAsignarVocal() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gestorcopaci/views/AsignarVocal.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.setTitle("Asignar Nuevo Vocal");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            
            // Recargar la tabla después de cerrar la ventana
            cargarVocales();
            
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir la ventana de asignación:\n" + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    @FXML
    private void handleRegistrarAsistencia() {
        Vocal vocalSeleccionado = tablaVocales.getSelectionModel().getSelectedItem();
        
        if (vocalSeleccionado == null) {
            mostrarAlerta("Selección requerida", "Por favor seleccione un vocal de la tabla", Alert.AlertType.WARNING);
            return;
        }
        
        if (!"activo".equals(vocalSeleccionado.getEstado())) {
            mostrarAlerta("Vocal no activo", "Solo se puede registrar asistencia a vocales activos", Alert.AlertType.WARNING);
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gestorcopaci/views/RegistrarAsistencia.fxml"));
            Parent root = loader.load();
            
            RegistrarAsistenciaController controller = loader.getController();
            controller.setVocal(vocalSeleccionado);
            
            Stage stage = new Stage();
            stage.setTitle("Registrar Asistencia - Vocal ID: " + vocalSeleccionado.getIdVocal());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            
            // Recargar la tabla después de registrar asistencia
            cargarVocales();
            
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir la ventana de asistencia:\n" + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    @FXML
    private void handleVerDetalles() {
        Vocal vocalSeleccionado = tablaVocales.getSelectionModel().getSelectedItem();
        
        if (vocalSeleccionado == null) {
            mostrarAlerta("Selección requerida", "Por favor seleccione un vocal de la tabla", Alert.AlertType.WARNING);
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gestorcopaci/views/DetallesVocal.fxml"));
            Parent root = loader.load();
            
            DetallesVocalController controller = loader.getController();
            controller.setVocal(vocalSeleccionado);
            
            Stage stage = new Stage();
            stage.setTitle("Detalles del Vocal - ID: " + vocalSeleccionado.getIdVocal());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir la ventana de detalles:\n" + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    @FXML
    private void handleActualizar() {
        cargarVocales();
        mostrarAlerta("Actualizado", "Datos actualizados correctamente", Alert.AlertType.INFORMATION);
    }
    
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}