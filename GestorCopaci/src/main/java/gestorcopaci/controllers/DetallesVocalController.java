/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gestorcopaci.controllers;

/**
 *
 * @author manue
 */

import gestorcopaci.daos.AsistenciaDAO;
import gestorcopaci.daos.VocalDAO;
import gestorcopaci.models.Asistencia;
import gestorcopaci.models.Vocal;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

public class DetallesVocalController implements Initializable {

    @FXML private Label lblIdVocal;
    @FXML private Label lblIdCiudadano;
    @FXML private Label lblFechaInicio;
    @FXML private Label lblFechaFin;
    @FXML private Label lblEventosAsistidos;
    @FXML private Label lblPorcentajeAsistencia;
    @FXML private Label lblEstado;
    @FXML private ProgressBar progressBar;
    
    @FXML private TableView<Asistencia> tablaAsistencias;
    @FXML private TableColumn<Asistencia, Integer> colIdAsistencia;
    @FXML private TableColumn<Asistencia, Integer> colIdEvento;
    @FXML private TableColumn<Asistencia, String> colFechaAsistencia;
    
    @FXML private Button btnCerrar;
    @FXML private Button btnActualizar;

    private Vocal vocal;
    private AsistenciaDAO asistenciaDAO;
    private ObservableList<Asistencia> asistenciasList;
    private SimpleDateFormat dateFormat;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        asistenciaDAO = new AsistenciaDAO();
        asistenciasList = FXCollections.observableArrayList();
        dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        
        configurarTabla();
    }
    
    public void setVocal(Vocal vocal) {
        this.vocal = vocal;
        cargarDatosVocal();
        cargarAsistencias();
    }
    
    private void cargarDatosVocal() {
        if (vocal != null) {
            lblIdVocal.setText(String.valueOf(vocal.getIdVocal()));
            lblIdCiudadano.setText(String.valueOf(vocal.getIdCiudadano()));
            lblFechaInicio.setText(dateFormat.format(vocal.getFechaInicioMandato()));
            lblFechaFin.setText(dateFormat.format(vocal.getFechaFinMandato()));
            lblEventosAsistidos.setText(vocal.getEventosAsistidos() + " / 10");
            lblPorcentajeAsistencia.setText(String.format("%.2f%%", vocal.getPorcentajeAsistencia()));
            lblEstado.setText(vocal.getEstado());
            
            // Configurar progress bar
            double progreso = vocal.getEventosAsistidos() / 10.0;
            progressBar.setProgress(progreso);
            
            // Colorear según estado
            switch (vocal.getEstado()) {
                case "activo":
                    lblEstado.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                    break;
                case "completado":
                    lblEstado.setStyle("-fx-text-fill: #3498db; -fx-font-weight: bold;");
                    break;
                case "incompleto":
                    lblEstado.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                    break;
            }
        }
    }
    
    private void configurarTabla() {
        colIdAsistencia.setCellValueFactory(new PropertyValueFactory<>("idAsistencia"));
        colIdEvento.setCellValueFactory(new PropertyValueFactory<>("idEvento"));
        colFechaAsistencia.setCellValueFactory(new PropertyValueFactory<>("fechaAsistencia"));
        
        tablaAsistencias.setItems(asistenciasList);
    }
    
    private void cargarAsistencias() {
        if (vocal != null) {
            asistenciasList.clear();
            asistenciasList.addAll(asistenciaDAO.obtenerAsistenciasPorVocal(vocal.getIdVocal()));
        }
    }
    
    @FXML
    private void handleActualizar() {
        if (vocal != null) {
            VocalDAO vocalDAO = new VocalDAO();
            vocalDAO.actualizarEstadisticasVocal(vocal.getIdVocal());
            
            // Recargar datos del vocal
            Vocal vocalActualizado = vocalDAO.obtenerVocalPorCiudadano(vocal.getIdCiudadano());
            if (vocalActualizado != null) {
                this.vocal = vocalActualizado;
                cargarDatosVocal();
            }
            
            cargarAsistencias();
            mostrarAlerta("Actualizado", "Datos actualizados correctamente", Alert.AlertType.INFORMATION);
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