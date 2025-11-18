package gestorcopaci.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {

    private static final String URL = "jdbc:mysql://127.0.0.1:3306/bd_copaci?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root"; // cambia si usas otro
    private static final String PASS = ""; // tu contraseña

    public static Connection getConexion() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
