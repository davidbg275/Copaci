package gestorcopaci.daos;

import gestorcopaci.models.Ciudadano;
import gestorcopaci.utils.DatabaseConnection;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CiudadanoDAO {

    public boolean insertarCiudadano(Ciudadano ciudadano) {
        String sql = "INSERT INTO ciudadanos (nombre, fecha_nacimiento, domicilio_actual, manzana, " +
                "estado_civil, ocupacion, tipo_ciudadano, fecha_alta, tipo_certificado, " +
                "lugar_nacimiento, grado_estudios) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, ciudadano.getNombre());
            pstmt.setDate(2, Date.valueOf(ciudadano.getFechaNacimiento()));
            pstmt.setString(3, ciudadano.getDomicilioActual());
            pstmt.setString(4, ciudadano.getManzana());
            pstmt.setString(5, ciudadano.getEstadoCivil());
            pstmt.setString(6, ciudadano.getOcupacion());
            pstmt.setString(7, ciudadano.getTipoCiudadano());
            pstmt.setDate(8, Date.valueOf(ciudadano.getFechaAlta()));
            pstmt.setString(9, ciudadano.getTipoCertificado());
            pstmt.setString(10, ciudadano.getLugarNacimiento());
            pstmt.setString(11, ciudadano.getGradoEstudios());

            int resultado = pstmt.executeUpdate();
            System.out.println("✅ Ciudadano insertado correctamente. Filas afectadas: " + resultado);
            return resultado > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error al insertar ciudadano: " + e.getMessage());
            System.err.println("🔍 SQL ejecutado: " + sql);
            e.printStackTrace();
            return false;
        }
    }

    public List<Ciudadano> obtenerTodosCiudadanos() {
        List<Ciudadano> ciudadanos = new ArrayList<>();
        String sql = "SELECT * FROM ciudadanos";

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Ciudadano ciudadano = new Ciudadano();
                ciudadano.setIdCiudadano(rs.getInt("id_ciudadano"));
                ciudadano.setNombre(rs.getString("nombre"));

                // Manejar fechas que puedan ser null
                Date fechaNac = rs.getDate("fecha_nacimiento");
                if (fechaNac != null) {
                    ciudadano.setFechaNacimiento(fechaNac.toLocalDate());
                }

                ciudadano.setDomicilioActual(rs.getString("domicilio_actual"));
                ciudadano.setManzana(rs.getString("manzana"));
                ciudadano.setEstadoCivil(rs.getString("estado_civil"));
                ciudadano.setOcupacion(rs.getString("ocupacion")); // ← Sin acento
                ciudadano.setTipoCiudadano(rs.getString("tipo_ciudadano"));

                Date fechaAlta = rs.getDate("fecha_alta");
                if (fechaAlta != null) {
                    ciudadano.setFechaAlta(fechaAlta.toLocalDate());
                }

                ciudadano.setTipoCertificado(rs.getString("tipo_certificado"));
                ciudadano.setLugarNacimiento(rs.getString("lugar_nacimiento"));
                ciudadano.setGradoEstudios(rs.getString("grado_estudios"));

                ciudadanos.add(ciudadano);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener ciudadanos: " + e.getMessage());
        }

        return ciudadanos;
    }

    public boolean actualizarCiudadano(Ciudadano ciudadano) {
        String sql = "UPDATE Ciudadanos SET nombre=?, fecha_nacimiento=?, domicilio_actual=?, " +
                "manzana=?, estado_civil=?, ocupacion=?, tipo_ciudadano=?, fecha_alta=?, " + // ← Sin acento
                "tipo_certificado=?, lugar_nacimiento=?, grado_estudios=? WHERE id_ciudadano=?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, ciudadano.getNombre());
            pstmt.setDate(2, Date.valueOf(ciudadano.getFechaNacimiento()));
            pstmt.setString(3, ciudadano.getDomicilioActual());
            pstmt.setString(4, ciudadano.getManzana());
            pstmt.setString(5, ciudadano.getEstadoCivil());
            pstmt.setString(6, ciudadano.getOcupacion());
            pstmt.setString(7, ciudadano.getTipoCiudadano());
            pstmt.setDate(8, Date.valueOf(ciudadano.getFechaAlta()));
            pstmt.setString(9, ciudadano.getTipoCertificado());
            pstmt.setString(10, ciudadano.getLugarNacimiento());
            pstmt.setString(11, ciudadano.getGradoEstudios());
            pstmt.setInt(12, ciudadano.getIdCiudadano());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar ciudadano: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarCiudadano(int idCiudadano) {
        String sql = "DELETE FROM Ciudadanos WHERE id_ciudadano=?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idCiudadano);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al eliminar ciudadano: " + e.getMessage());
            return false;
        }
    }
}