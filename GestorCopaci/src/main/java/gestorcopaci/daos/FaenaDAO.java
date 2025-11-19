/*
 * DAO para tabla faenas
 */
package gestorcopaci.daos;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import gestorcopaci.models.Faena;
import gestorcopaci.utils.DatabaseConnection;

public class FaenaDAO {

    public boolean insertarFaena(Faena faena) {
        String sql = "INSERT INTO faenas (anio, mes, asistencia, pago_reposicion, fecha_registro, id_ciudadano) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, faena.getAnio());
            pstmt.setString(2, faena.getMes());
            pstmt.setBoolean(3, faena.isAsistencia());
            pstmt.setDouble(4, faena.getPagoReposicion());
            pstmt.setDate(5, Date.valueOf(faena.getFechaRegistro()));
            pstmt.setInt(6, faena.getIdCiudadano());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al insertar faena: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<Faena> obtenerFaenasPorCiudadanoYAnio(int idCiudadano, int anio) {
        List<Faena> lista = new ArrayList<>();
        String sql = "SELECT * FROM faenas WHERE id_ciudadano = ? AND anio = ? ORDER BY id_faena";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idCiudadano);
            pstmt.setInt(2, anio);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                lista.add(mapearFaena(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener faenas: " + e.getMessage());
        }

        return lista;
    }

    public List<Faena> obtenerFaenasPorCiudadano(int idCiudadano) {
        List<Faena> lista = new ArrayList<>();
        String sql = "SELECT * FROM faenas WHERE id_ciudadano = ? ORDER BY anio, id_faena";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idCiudadano);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                lista.add(mapearFaena(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener faenas por ciudadano: " + e.getMessage());
        }

        return lista;
    }

    private Faena mapearFaena(ResultSet rs) throws SQLException {
        Faena f = new Faena();
        f.setIdFaena(rs.getInt("id_faena"));
        f.setAnio(rs.getInt("anio"));
        f.setMes(rs.getString("mes"));
        f.setAsistencia(rs.getBoolean("asistencia"));
        f.setPagoReposicion(rs.getDouble("pago_reposicion"));

        Date fecha = rs.getDate("fecha_registro");
        if (fecha != null) {
            f.setFechaRegistro(fecha.toLocalDate());
        } else {
            f.setFechaRegistro(LocalDate.now());
        }

        f.setIdCiudadano(rs.getInt("id_ciudadano"));
        return f;
    }
}
