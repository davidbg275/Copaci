/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gestorcopaci.daos;

/**
 *
 * @author manue
 */

import gestorcopaci.models.Vocal;
import gestorcopaci.utils.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VocalDAO {
    private Connection connection;
    
    public VocalDAO() {
        this.connection = DatabaseConnection.getConnection();
    }
    
    // Agrega este método para obtener el nombre del ciudadano
    public String obtenerNombreCiudadano(int idCiudadano) {
        String sql = "SELECT nombre FROM ciudadanos WHERE id_ciudadano = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idCiudadano);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("nombre");
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener nombre del ciudadano: " + e.getMessage());
        }
        return "Ciudadano ID: " + idCiudadano;
    }

    public boolean esVocalActivo(int idCiudadano) {
        String sql = "SELECT 1 FROM vocales " +
                     "WHERE id_ciudadano = ? AND estado = 'activo' " +
                     "LIMIT 1";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idCiudadano);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); // hay al menos un registro activo
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar si es vocal activo: " + e.getMessage());
            return false;
        }
    }
    
    
    // ... (tus otros métodos existentes)
    
    public boolean asignarVocal(int idCiudadano) {
        String sql = "INSERT INTO vocales (id_ciudadano, fecha_inicio_mandato, fecha_fin_mandato) VALUES (?, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 YEAR))";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idCiudadano);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error al asignar vocal: " + e.getMessage());
            return false;
        }
    }
    
    public Vocal obtenerVocalPorCiudadano(int idCiudadano) {
        String sql = "SELECT * FROM vocales WHERE id_ciudadano = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idCiudadano);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToVocal(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener vocal: " + e.getMessage());
        }
        return null;
    }
    
    public List<Vocal> obtenerVocalesActivos() {
        List<Vocal> vocales = new ArrayList<>();
        String sql = "SELECT * FROM vocales WHERE estado = 'activo'";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                vocales.add(mapResultSetToVocal(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener vocales activos: " + e.getMessage());
        }
        return vocales;
    }
    
    public List<Vocal> obtenerTodosVocales() {
        List<Vocal> vocales = new ArrayList<>();
        String sql = "SELECT * FROM vocales ORDER BY created_at DESC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                vocales.add(mapResultSetToVocal(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener todos los vocales: " + e.getMessage());
        }
        return vocales;
    }
    
    public boolean actualizarEstadisticasVocal(int idVocal) {
        String sql = "UPDATE vocales SET " +
                     "eventos_asistidos = (SELECT COUNT(*) FROM asistencias WHERE id_vocal = ?), " +
                     "porcentaje_asistencia = (SELECT (COUNT(*) * 100.0 / 10) FROM asistencias WHERE id_vocal = ?), " +
                     "estado = CASE " +
                     "    WHEN (SELECT COUNT(*) FROM asistencias WHERE id_vocal = ?) >= 10 " +
                     "    AND (SELECT (COUNT(*) * 100.0 / 10) FROM asistencias WHERE id_vocal = ?) > 90 " +
                     "    THEN 'completado' " +
                     "    WHEN fecha_fin_mandato < CURDATE() THEN 'incompleto' " +
                     "    ELSE 'activo' " +
                     "END " +
                     "WHERE id_vocal = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idVocal);
            stmt.setInt(2, idVocal);
            stmt.setInt(3, idVocal);
            stmt.setInt(4, idVocal);
            stmt.setInt(5, idVocal);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar estadísticas: " + e.getMessage());
            return false;
        }
    }
    
    private Vocal mapResultSetToVocal(ResultSet rs) throws SQLException {
        Vocal vocal = new Vocal();
        vocal.setIdVocal(rs.getInt("id_vocal"));
        vocal.setIdCiudadano(rs.getInt("id_ciudadano"));
        vocal.setFechaInicioMandato(rs.getDate("fecha_inicio_mandato"));
        vocal.setFechaFinMandato(rs.getDate("fecha_fin_mandato"));
        vocal.setEventosAsistidos(rs.getInt("eventos_asistidos"));
        vocal.setPorcentajeAsistencia(rs.getDouble("porcentaje_asistencia"));
        vocal.setEstado(rs.getString("estado"));
        vocal.setCreatedAt(rs.getTimestamp("created_at"));
        vocal.setUpdatedAt(rs.getTimestamp("updated_at"));
        return vocal;
    }
}