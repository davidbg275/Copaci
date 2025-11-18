package gestorcopaci.daos;

import gestorcopaci.models.Donacion;
import gestorcopaci.utils.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DonacionDAO {

    public boolean insertarDonacion(Donacion donacion) {
        String sql = "INSERT INTO donaciones (id_ciudadano, concepto, monto, fecha) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, donacion.getIdCiudadano());
            pstmt.setString(2, donacion.getConcepto());
            pstmt.setDouble(3, donacion.getMonto());
            pstmt.setDate(4, Date.valueOf(donacion.getFecha()));

            int resultado = pstmt.executeUpdate();
            System.out.println("✅ Donación insertada correctamente. Filas afectadas: " + resultado);
            return resultado > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error al insertar donación: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<Donacion> obtenerTodasDonaciones() {
        List<Donacion> donaciones = new ArrayList<>();
        String sql = "SELECT id_donacion, id_ciudadano, concepto, monto, fecha FROM donaciones";

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Donacion donacion = new Donacion();
                donacion.setIdExtra(rs.getInt("id_donacion")); // mapeamos id_donacion -> idExtra
                donacion.setIdCiudadano(rs.getInt("id_ciudadano"));
                donacion.setConcepto(rs.getString("concepto"));
                donacion.setMonto(rs.getDouble("monto"));

                Date fecha = rs.getDate("fecha");
                if (fecha != null) {
                    donacion.setFecha(fecha.toLocalDate());
                }

                donaciones.add(donacion);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener donaciones: " + e.getMessage());
        }

        return donaciones;
    }

    public List<Donacion> obtenerDonacionesPorCiudadano(int idCiudadano) {
        List<Donacion> donaciones = new ArrayList<>();
        String sql = "SELECT id_donacion, id_ciudadano, concepto, monto, fecha FROM donaciones WHERE id_ciudadano = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idCiudadano);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Donacion donacion = new Donacion();
                donacion.setIdExtra(rs.getInt("id_donacion"));
                donacion.setIdCiudadano(rs.getInt("id_ciudadano"));
                donacion.setConcepto(rs.getString("concepto"));
                donacion.setMonto(rs.getDouble("monto"));

                Date fecha = rs.getDate("fecha");
                if (fecha != null) {
                    donacion.setFecha(fecha.toLocalDate());
                }

                donaciones.add(donacion);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener donaciones por ciudadano: " + e.getMessage());
        }

        return donaciones;
    }

    public double obtenerTotalDonaciones() {
        String sql = "SELECT SUM(monto) AS total FROM donaciones";

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getDouble("total");
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener total de donaciones: " + e.getMessage());
        }

        return 0.0;
    }

    public boolean eliminarDonacion(int idExtra) {
        String sql = "DELETE FROM donaciones WHERE id_donacion = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idExtra);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al eliminar donación: " + e.getMessage());
            return false;
        }
    }
}
