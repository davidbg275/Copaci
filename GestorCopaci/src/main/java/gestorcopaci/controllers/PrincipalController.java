package gestorcopaci.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import gestorcopaci.App;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class PrincipalController implements Initializable {

    @FXML
    private ImageView imgLogo;

    @FXML
    private Label lblTitulo;

    @FXML
    private Label lblContenido;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Image logo = new Image(
                App.class.getResourceAsStream("images/logo1.jpg")
        );
        imgLogo.setImage(logo);
    }

    @FXML
    private void onDashboard() {
        lblTitulo.setText("Bienvenido al Sistema Copaci");
        lblContenido.setText("Dashboard principal (pendiente de implementar)");
    }

    @FXML
    private void onRegistro() {
        try {
            // Cargar la interfaz de registro de ciudadanos
            FXMLLoader loader = new FXMLLoader(
                    App.class.getResource("views/registro_ciudadanos.fxml")
            );
            Parent root = loader.load();
            
            // Crear nueva ventana
            Stage stage = new Stage();
            stage.setTitle("Registro de Ciudadanos - Sistema Copaci");
            stage.setScene(new Scene(root));
            stage.setResizable(true);
            
            // Configurar para que se cierre correctamente
            stage.setOnCloseRequest(event -> {
                lblTitulo.setText("Bienvenido al Sistema Copaci");
                lblContenido.setText("Registro de ciudadanos cerrado");
            });
            
            stage.show();
            
            // Actualizar la interfaz principal
            lblTitulo.setText("Módulo de Registro - Ciudadanos");
            lblContenido.setText("Ventana de registro de ciudadanos abierta. Puede minimizar esta ventana para acceder al formulario.");
            
        } catch (Exception e) {
            e.printStackTrace();
            error("Error", "No se pudo abrir el módulo de registro:\n" + e.getMessage());
        }
    }

@FXML
private void onFaenas() {
    try {
        FXMLLoader loader = new FXMLLoader(
                App.class.getResource("views/faenas.fxml")
        );
        Parent root = loader.load();

        Stage stage = new Stage();
        stage.setTitle("Registro de Faenas - Sistema Copaci");
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.show();

        lblTitulo.setText("Módulo de Faenas");
        lblContenido.setText("Ventana de registro de faenas abierta.");

    } catch (Exception e) {
        e.printStackTrace();
        error("Error", "No se pudo abrir el módulo de faenas:\n" + e.getMessage());
    }
}


    @FXML
private void onCooperaciones() {
    try {
        FXMLLoader loader = new FXMLLoader(
                App.class.getResource("views/cooperaciones.fxml")
        );
        Parent root = loader.load();
        
        Stage stage = new Stage();
        stage.setTitle("Registro de Cooperaciones - Sistema Copaci");
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        
        stage.show();
        
        // Actualizar la interfaz principal
        lblTitulo.setText("Módulo de Cooperaciones");
        lblContenido.setText("Ventana de registro de cooperaciones abierta.");
        
    } catch (Exception e) {
        e.printStackTrace();
        error("Error", "No se pudo abrir el módulo de cooperaciones:\n" + e.getMessage());
    }
}

    @FXML
private void onDonaciones() {
    try {
        FXMLLoader loader = new FXMLLoader(
                App.class.getResource("views/donaciones.fxml")
        );
        Parent root = loader.load();
        
        Stage stage = new Stage();
        stage.setTitle("Registro de Donaciones - Sistema Copaci");
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        
        stage.show();
        
        // Actualizar la interfaz principal
        lblTitulo.setText("Módulo de Donaciones");
        lblContenido.setText("Ventana de registro de donaciones abierta.");
        
    } catch (Exception e) {
        e.printStackTrace();
        error("Error", "No se pudo abrir el módulo de donaciones:\n" + e.getMessage());
    }
}

    @FXML
    private void onBitacora() {
        lblTitulo.setText("Módulo de Asistencia");
        lblContenido.setText("Aquí irá la bitácora de asistencias.");
        info("Asistencia", "Redirigiendo a: Módulo de Asistencia (por implementar)");
    }

    @FXML
    private void onUsuarios() {
        lblTitulo.setText("Módulo de Usuarios");
        lblContenido.setText("Aquí irá el manejo de usuarios.");
        info("Usuarios", "Redirigiendo a: Módulo de Usuarios (por implementar)");
    }

    @FXML
    private void onCerrarSesion() {
        Stage stage = (Stage) lblTitulo.getScene().getWindow();
        try {
            FXMLLoader loader = new FXMLLoader(
                    App.class.getResource("views/login.fxml")
            );
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.setTitle("Copaci - Login");
            stage.setMaximized(false);
        } catch (Exception e) {
            e.printStackTrace();
            error("Error", "No se pudo regresar al login:\n" + e.getMessage());
        }
    }

    private void info(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void error(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}