package gestorcopaci.daos;

import gestorcopaci.models.Asistencia;
import gestorcopaci.utils.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AsistenciaDAO {
    private Connection connection;
    
    public AsistenciaDAO() {
        this.connection = DatabaseConnection.getConnection();
    }
    
    // Registrar asistencia de un vocal a un evento
    public boolean registrarAsistencia(int idVocal, int idEvento) {
        String sql = "INSERT INTO asistencias (id_vocal, id_evento, fecha_asistencia) VALUES (?, ?, CURDATE())";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idVocal);
            stmt.setInt(2, idEvento);
            
            int rowsAffected = stmt.executeUpdate();
            
            // Actualizar estadísticas del vocal después de registrar asistencia
            if (rowsAffected > 0) {
                VocalDAO vocalDAO = new VocalDAO(); // Esto funciona porque están en el mismo paquete
                vocalDAO.actualizarEstadisticasVocal(idVocal);
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error al registrar asistencia: " + e.getMessage());
        }
        return false;
    }
    
    // ... (el resto del código permanece igual)
    
    // Verificar si un vocal ya asistió a un evento
    public boolean verificarAsistenciaExistente(int idVocal, int idEvento) {
        String sql = "SELECT COUNT(*) FROM asistencias WHERE id_vocal = ? AND id_evento = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idVocal);
            stmt.setInt(2, idEvento);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar asistencia: " + e.getMessage());
        }
        return false;
    }
    
    // Obtener asistencias de un vocal
    public List<Asistencia> obtenerAsistenciasPorVocal(int idVocal) {
        List<Asistencia> asistencias = new ArrayList<>();
        String sql = "SELECT a.*, e.nombre_evento FROM asistencias a " +
                     "INNER JOIN eventos e ON a.id_evento = e.id_evento " +
                     "WHERE a.id_vocal = ? ORDER BY a.fecha_asistencia DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idVocal);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Asistencia asistencia = mapResultSetToAsistencia(rs);
                asistencias.add(asistencia);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener asistencias: " + e.getMessage());
        }
        return asistencias;
    }
    
    // Contar asistencias de un vocal
    public int contarAsistenciasVocal(int idVocal) {
        String sql = "SELECT COUNT(*) FROM asistencias WHERE id_vocal = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idVocal);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
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