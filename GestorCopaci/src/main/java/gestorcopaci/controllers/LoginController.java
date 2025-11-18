package gestorcopaci.controllers;

import gestorcopaci.App;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private TextField txtUsuario;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private void onIngresar() {
        String usuario = txtUsuario.getText().trim();
        String password = txtPassword.getText();

        if (usuario.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING,
                    "Error de validación",
                    "Por favor, complete todos los campos");
            return;
        }

        // Aquí luego conectas con BD. Por ahora, lo mismo que tenían:
        if (usuario.equals("admin") && password.equals("1234")) {
            showAlert(Alert.AlertType.INFORMATION,
                    "Login exitoso",
                    "¡Bienvenido a Copaci!");

            cargarPrincipal();

        } else {
            showAlert(Alert.AlertType.ERROR,
                    "Error de autenticación",
                    "Usuario o contraseña incorrectos");
            txtPassword.clear();
            txtUsuario.requestFocus();
        }
    }

    @FXML
    private void onSalir() {
        Stage stage = (Stage) txtUsuario.getScene().getWindow();
        stage.close();
    }

    private void cargarPrincipal() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    App.class.getResource("views/principal.fxml")
            );
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) txtUsuario.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Copaci - Principal");
            stage.setMaximized(true);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR,
                    "Error",
                    "No se pudo cargar la ventana principal:\n" + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
