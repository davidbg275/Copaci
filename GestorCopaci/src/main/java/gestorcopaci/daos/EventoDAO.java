/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gestorcopaci.daos;


import gestorcopaci.models.Evento;
import gestorcopaci.utils.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventoDAO {
    private Connection connection;
    
    public EventoDAO() {
        this.connection = DatabaseConnection.getConnection();
    }
    
    // Crear nuevo evento
    public boolean crearEvento(String nombreEvento, Date fechaEvento) {
        String sql = "INSERT INTO eventos (nombre_evento, fecha_evento) VALUES (?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, nombreEvento);
            stmt.setDate(2, new java.sql.Date(fechaEvento.getTime()));
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error al crear evento: " + e.getMessage());
            return false;
        }
    }
    
    // Obtener todos los eventos activos
    public List<Evento> obtenerEventosActivos() {
        List<Evento> eventos = new ArrayList<>();
        String sql = "SELECT * FROM eventos WHERE estado_evento = 'activo' ORDER BY fecha_evento DESC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                eventos.add(mapResultSetToEvento(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener eventos activos: " + e.getMessage());
        }
        return eventos;
    }
    
    // Obtener eventos disponibles para asistencia (no pasados)
    public List<Evento> obtenerEventosDisponibles() {
        List<Evento> eventos = new ArrayList<>();
        String sql = "SELECT * FROM eventos WHERE estado_evento = 'activo' AND fecha_evento <= CURDATE() ORDER BY fecha_evento DESC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                eventos.add(mapResultSetToEvento(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener eventos disponibles: " + e.getMessage());
        }
        return eventos;
    }
    
    // Obtener evento por ID
    public Evento obtenerEventoPorId(int idEvento) {
        String sql = "SELECT * FROM eventos WHERE id_evento = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idEvento);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToEvento(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener evento: " + e.getMessage());
        }
        return null;
    }
    
    // Método auxiliar para mapear ResultSet a objeto Evento
    private Evento mapResultSetToEvento(ResultSet rs) throws SQLException {
        Evento evento = new Evento();
        evento.setIdEvento(rs.getInt("id_evento"));
        evento.setNombreEvento(rs.getString("nombre_evento"));
        evento.setFechaEvento(rs.getDate("fecha_evento"));
        evento.setValorEvento(rs.getInt("valor_evento"));
        evento.setEstadoEvento(rs.getString("estado_evento"));
        evento.setCreatedAt(rs.getTimestamp("created_at"));
        return evento;
    }
}