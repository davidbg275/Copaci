package gestorcopaci.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import gestorcopaci.models.Evento;
import gestorcopaci.utils.DatabaseConnection;

public class EventoDAO {

    public EventoDAO() {
        // No guardamos la conexión en un campo
    }

    // Crear un nuevo evento
    public boolean crearEvento(Evento evento) {
        String sql = "INSERT INTO eventos (nombre_evento, fecha_evento, valor_evento, estado_evento) " +
                     "VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, evento.getNombreEvento());
            stmt.setDate(2, new java.sql.Date(evento.getFechaEvento().getTime()));
            stmt.setInt(3, evento.getValorEvento());
            stmt.setString(4, evento.getEstadoEvento());

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        evento.setIdEvento(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error al crear evento: " + e.getMessage());
        }
        return false;
    }

    // Obtener todos los eventos
    public List<Evento> obtenerTodosEventos() {
        List<Evento> eventos = new ArrayList<>();
        String sql = "SELECT * FROM eventos ORDER BY fecha_evento ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                eventos.add(mapResultSetToEvento(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener todos los eventos: " + e.getMessage());
        }

        return eventos;
    }

    // Obtener solo eventos activos (disponibles) – ESTE ES EL QUE TE ESTÁ TRONANDO
    public List<Evento> obtenerEventosDisponibles() {
        List<Evento> eventos = new ArrayList<>();
        String sql = "SELECT * FROM eventos WHERE estado_evento = 'activo' ORDER BY fecha_evento ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

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

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idEvento);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEvento(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener evento por ID: " + e.getMessage());
        }
        return null;
    }

    // Actualizar evento
    public boolean actualizarEvento(Evento evento) {
        String sql = "UPDATE eventos SET nombre_evento = ?, fecha_evento = ?, " +
                     "valor_evento = ?, estado_evento = ? WHERE id_evento = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, evento.getNombreEvento());
            stmt.setDate(2, new java.sql.Date(evento.getFechaEvento().getTime()));
            stmt.setInt(3, evento.getValorEvento());
            stmt.setString(4, evento.getEstadoEvento());
            stmt.setInt(5, evento.getIdEvento());

            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar evento: " + e.getMessage());
            return false;
        }
    }

    // Eliminar evento
    public boolean eliminarEvento(int idEvento) {
        String sql = "DELETE FROM eventos WHERE id_evento = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idEvento);
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar evento: " + e.getMessage());
            return false;
        }
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
