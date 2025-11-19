package gestorcopaci.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import gestorcopaci.models.Asistencia;
import gestorcopaci.utils.DatabaseConnection;

public class AsistenciaDAO {

    public AsistenciaDAO() {
        // No guardamos la conexión en un campo
    }

    // Registrar asistencia de un vocal a un evento
    public boolean registrarAsistencia(int idVocal, int idEvento) {
        String sql = "INSERT INTO asistencias (id_vocal, id_evento, fecha_asistencia) " +
                     "VALUES (?, ?, CURDATE())";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idVocal);
            stmt.setInt(2, idEvento);

            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Error al registrar asistencia: " + e.getMessage());
            return false;
        }
    }

    // Verificar si ya existe asistencia para ese vocal y evento
    public boolean verificarAsistenciaExistente(int idVocal, int idEvento) {
        String sql = "SELECT 1 FROM asistencias WHERE id_vocal = ? AND id_evento = ? LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idVocal);
            stmt.setInt(2, idEvento);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar asistencia existente: " + e.getMessage());
            return false;
        }
    }

    // Obtener todas las asistencias de un vocal
    public List<Asistencia> obtenerAsistenciasPorVocal(int idVocal) {
        List<Asistencia> asistencias = new ArrayList<>();
        String sql = "SELECT * FROM asistencias WHERE id_vocal = ? ORDER BY fecha_asistencia ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idVocal);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    asistencias.add(mapResultSetToAsistencia(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener asistencias por vocal: " + e.getMessage());
        }

        return asistencias;
    }

    // Obtener el total de asistencias de un vocal (por si lo necesitas)
    public int contarAsistenciasPorVocal(int idVocal) {
        String sql = "SELECT COUNT(*) AS total FROM asistencias WHERE id_vocal = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idVocal);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al contar asistencias: " + e.getMessage());
        }

        return 0;
    }

    // Método auxiliar para mapear ResultSet a objeto Asistencia
    private Asistencia mapResultSetToAsistencia(ResultSet rs) throws SQLException {
        Asistencia asistencia = new Asistencia();
        asistencia.setIdAsistencia(rs.getInt("id_asistencia"));
        asistencia.setIdVocal(rs.getInt("id_vocal"));
        asistencia.setIdEvento(rs.getInt("id_evento"));
        asistencia.setFechaAsistencia(rs.getDate("fecha_asistencia"));
        asistencia.setCreatedAt(rs.getTimestamp("created_at"));
        return asistencia;
    }
}
