package gestorcopaci.controllers;

import gestorcopaci.App;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import java.net.URL;
import java.util.ResourceBundle;

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
        lblTitulo.setText("Módulo de Registro");
        lblContenido.setText("Aquí irá el formulario de ciudadanos / registro.");
        info("Registro", "Redirigiendo a: Módulo de Registro (por implementar)");
    }

    @FXML
    private void onFaenas() {
        lblTitulo.setText("Módulo de Faenas");
        lblContenido.setText("Aquí irá el manejo de faenas.");
        info("Faenas", "Redirigiendo a: Módulo de Faenas (por implementar)");
    }

    @FXML
    private void onCooperaciones() {
        lblTitulo.setText("Módulo de Cooperaciones");
        lblContenido.setText("Aquí irá el manejo de cooperaciones.");
        info("Cooperaciones", "Redirigiendo a: Módulo de Cooperaciones (por implementar)");
    }

    @FXML
    private void onDonaciones() {
        lblTitulo.setText("Módulo de Donaciones");
        lblContenido.setText("Aquí irá el manejo de donaciones.");
        info("Donaciones", "Redirigiendo a: Módulo de Donaciones (por implementar)");
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
