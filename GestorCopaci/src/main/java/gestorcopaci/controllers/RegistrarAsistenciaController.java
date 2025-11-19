package gestorcopaci.controllers;

import gestorcopaci.daos.AsistenciaDAO;
import gestorcopaci.daos.EventoDAO;
import gestorcopaci.daos.VocalDAO;
import gestorcopaci.models.Evento;
import gestorcopaci.models.Vocal;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class RegistrarAsistenciaController implements Initializable {

    @FXML private Label lblVocalInfo;
    @FXML private TableView<Evento> tablaEventos;
    @FXML private TableColumn<Evento, Integer> colIdEvento;
    @FXML private TableColumn<Evento, String> colNombreEvento;
    @FXML private TableColumn<Evento, String> colFechaEvento;
    
    @FXML private Button btnRegistrarAsistencia;
    @FXML private Button btnCerrar;
    @FXML private ProgressBar progressAsistencia;
    @FXML private Label lblProgreso;

    private Vocal vocal;
    private AsistenciaDAO asistenciaDAO;
    private EventoDAO eventoDAO;
    private VocalDAO vocalDAO;
    private ObservableList<Evento> eventosList;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        asistenciaDAO = new AsistenciaDAO();
        eventoDAO = new EventoDAO();
        vocalDAO = new VocalDAO();
        eventosList = FXCollections.observableArrayList();
        
        configurarTabla();
    }
    
    public void setVocal(Vocal vocal) {
        this.vocal = vocal;
        actualizarInformacionVocal();
        cargarEventos();
    }
    
    private void actualizarInformacionVocal() {
        if (vocal != null) {
            lblVocalInfo.setText("Vocal: " + vocalDAO.obtenerNombreCiudadano(vocal.getIdCiudadano()) + 
                               " | Eventos asistidos: " + vocal.getEventosAsistidos() + "/10");
            actualizarProgreso();
        }
    }
    
    private void actualizarProgreso() {
        if (vocal != null) {
            double progreso = vocal.getEventosAsistidos() / 10.0;
            progressAsistencia.setProgress(progreso);
            lblProgreso.setText(vocal.getEventosAsistidos() + "/10 eventos (" + String.format("%.1f", progreso * 100) + "%)");
        }
    }
    
    private void configurarTabla() {
        colIdEvento.setCellValueFactory(new PropertyValueFactory<>("idEvento"));
        colNombreEvento.setCellValueFactory(new PropertyValueFactory<>("nombreEvento"));
        colFechaEvento.setCellValueFactory(new PropertyValueFactory<>("fechaEvento"));
        
        // Configurar estilo para las filas según si ya asistió
        tablaEventos.setRowFactory(tv -> new TableRow<Evento>() {
            @Override
            protected void updateItem(Evento evento, boolean empty) {
                super.updateItem(evento, empty);
                if (evento == null || empty || vocal == null) {
                    setStyle("");
                } else {
                    boolean yaAsistio = asistenciaDAO.verificarAsistenciaExistente(vocal.getIdVocal(), evento.getIdEvento());
                    if (yaAsistio) {
                        setStyle("-fx-background-color: #e8f5e8;"); // Verde claro para asistidos
                    } else {
                        setStyle(""); // Normal para pendientes
                    }
                }
            }
        });
        
        tablaEventos.setItems(eventosList);
        
        // Selección única
        tablaEventos.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }
    
    private void cargarEventos() {
        eventosList.clear();
        eventosList.addAll(eventoDAO.obtenerEventosDisponibles());
    }
    
    @FXML
    private void handleRegistrarAsistencia() {
        Evento eventoSeleccionado = tablaEventos.getSelectionModel().getSelectedItem();
        
        if (eventoSeleccionado == null) {
            mostrarAlerta("Error", "Por favor seleccione un evento de la tabla", Alert.AlertType.WARNING);
            return;
        }
        
        if (vocal == null) {
            mostrarAlerta("Error", "No se ha seleccionado un vocal", Alert.AlertType.ERROR);
            return;
        }
        
        // Verificar si ya existe la asistencia
        if (asistenciaDAO.verificarAsistenciaExistente(vocal.getIdVocal(), eventoSeleccionado.getIdEvento())) {
            mostrarAlerta("Asistencia ya registrada", 
                         "Este vocal ya tiene asistencia registrada para el evento:\n" + 
                         eventoSeleccionado.getNombreEvento(), 
                         Alert.AlertType.WARNING);
            return;
        }
        
        // Registrar la asistencia
        boolean exito = asistenciaDAO.registrarAsistencia(vocal.getIdVocal(), eventoSeleccionado.getIdEvento());
        
        if (exito) {
            mostrarAlerta("Éxito", 
                         "Asistencia registrada correctamente para:\n" + 
                         eventoSeleccionado.getNombreEvento(), 
                         Alert.AlertType.INFORMATION);
            
            // Actualizar información del vocal
            vocalDAO.actualizarEstadisticasVocal(vocal.getIdVocal());
            Vocal vocalActualizado = vocalDAO.obtenerVocalPorCiudadano(vocal.getIdCiudadano());
            if (vocalActualizado != null) {
                this.vocal = vocalActualizado;
                actualizarInformacionVocal();
            }
            
            // Actualizar la tabla para reflejar el cambio
            cargarEventos();
            
            // Verificar si completó el mandato
            if ("completado".equals(vocal.getEstado())) {
                mostrarAlerta("¡Felicidades!", 
                             "El vocal ha completado su mandato exitosamente\n" +
                             "Eventos asistidos: " + vocal.getEventosAsistidos() + "/10\n" +
                             "Porcentaje: " + String.format("%.2f", vocal.getPorcentajeAsistencia()) + "%", 
                             Alert.AlertType.INFORMATION);
            }
            
        } else {
            mostrarAlerta("Error", "No se pudo registrar la asistencia", Alert.AlertType.ERROR);
        }
    }
    
    @FXML
    private void handleCerrar() {
        Stage stage = (Stage) btnCerrar.getScene().getWindow();
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