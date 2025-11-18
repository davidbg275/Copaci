package gestorcopaci.controllers;

import gestorcopaci.App;
import gestorcopaci.db.ConexionBD;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

        if (credencialesValidas(usuario, password)) {
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
                    App.class.getResource("views/principal.fxml"));
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

    private boolean credencialesValidas(String usuario, String password) {
        String sql = "SELECT password FROM usuarios WHERE usuario = ?";

        try (Connection cn = ConexionBD.getConexion();
                PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, usuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return false; // no existe el usuario
                }
                String passBD = rs.getString("password");
                return password.equals(passBD); // por ahora sin hash
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR,
                    "Error de conexión",
                    "No se pudo conectar a la base de datos:\n" + e.getMessage());
            return false;
        }
    }

}
