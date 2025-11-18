/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gestorcopaci.daos;

import gestorcopaci.models.Cooperacion;
import gestorcopaci.utils.DatabaseConnection;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CooperacionDAO {

    // Precios base
    private static final double PRECIO_BANDA = 400.0;
    private static final double PRECIO_CASTILLO = 400.0;
    private static final double PRECIO_PASEO = 200.0;

    public boolean insertarCooperacion(Cooperacion cooperacion) {
    String sql = "INSERT INTO cooperaciones (anio, banda, castillo, paseo, cooperacion_extra, concepto_extra, " + // ← Agregado concepto_extra
                "fecha_registro, descuento, total_pagado, tipo_pago, id_ciudadano) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"; // ← Un parámetro más
    
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        pstmt.setInt(1, cooperacion.getAnio());
        pstmt.setDouble(2, cooperacion.getBanda());
        pstmt.setDouble(3, cooperacion.getCastillo());
        pstmt.setDouble(4, cooperacion.getPaseo());
        pstmt.setInt(5, cooperacion.getCooperacionExtra());
        pstmt.setString(6, cooperacion.getConceptoExtra()); // ← Nuevo campo
        pstmt.setDate(7, Date.valueOf(cooperacion.getFechaRegistro()));
        pstmt.setString(8, cooperacion.getDescuento());
        pstmt.setDouble(9, cooperacion.getTotalPagado());
        pstmt.setString(10, cooperacion.getTipoPago());
        pstmt.setInt(11, cooperacion.getIdCiudadano());
        
        int resultado = pstmt.executeUpdate();
        System.out.println("✅ Cooperación insertada correctamente. Filas afectadas: " + resultado);
        return resultado > 0;
        
    } catch (SQLException e) {
        System.err.println("❌ Error al insertar cooperación: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
}


    // Método para contar cooperaciones por tipo y año
    public int contarCooperacionesPorTipo(int idCiudadano, int anio, String tipo) {
        String sql = "SELECT COUNT(*) FROM cooperaciones WHERE id_ciudadano = ? AND anio = ? AND " + tipo + " > 0";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idCiudadano);
            pstmt.setInt(2, anio);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al contar cooperaciones de " + tipo + ": " + e.getMessage());
        }
        
        return 0;
    }

    // Verificar límites por tipo
    public String verificarLimitesCooperacion(int idCiudadano, int anio, boolean banda, boolean castillo, boolean paseo) {
        if (banda) {
            int countBanda = contarCooperacionesPorTipo(idCiudadano, anio, "banda");
            if (countBanda >= 2) {
                return "Ya tiene 2 cooperaciones de Banda registradas para este año";
            }
        }
        
        if (castillo) {
            int countCastillo = contarCooperacionesPorTipo(idCiudadano, anio, "castillo");
            if (countCastillo >= 2) {
                return "Ya tiene 2 cooperaciones de Castillo registradas para este año";
            }
        }
        
        if (paseo) {
            int countPaseo = contarCooperacionesPorTipo(idCiudadano, anio, "paseo");
            if (countPaseo >= 2) {
                return "Ya tiene 2 cooperaciones de Paseo registradas para este año";
            }
        }
        
        return null; // No hay problemas
    }

    public List<Cooperacion> obtenerCooperacionesPorCiudadano(int idCiudadano) {
        List<Cooperacion> cooperaciones = new ArrayList<>();
        String sql = "SELECT * FROM cooperaciones WHERE id_ciudadano = ? ORDER BY anio DESC, fecha_registro DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idCiudadano);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                cooperaciones.add(crearCooperacionDesdeResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener cooperaciones: " + e.getMessage());
        }
        
        return cooperaciones;
    }

    // Obtener resumen de cooperaciones por año
    public String obtenerResumenCooperaciones(int idCiudadano, int anio) {
        String sql = "SELECT " +
                    "SUM(CASE WHEN banda > 0 THEN 1 ELSE 0 END) as bandas, " +
                    "SUM(CASE WHEN castillo > 0 THEN 1 ELSE 0 END) as castillos, " +
                    "SUM(CASE WHEN paseo > 0 THEN 1 ELSE 0 END) as paseos " +
                    "FROM cooperaciones WHERE id_ciudadano = ? AND anio = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idCiudadano);
            pstmt.setInt(2, anio);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                int bandas = rs.getInt("bandas");
                int castillos = rs.getInt("castillos");
                int paseos = rs.getInt("paseos");
                
                return String.format("Banda: %d/2 | Castillo: %d/2 | Paseo: %d/2", bandas, castillos, paseos);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener resumen: " + e.getMessage());
        }
        
        return "Banda: 0/2 | Castillo: 0/2 | Paseo: 0/2";
    }

    public double calcularTotalConDescuento(String tipoCiudadano, boolean banda, boolean castillo, boolean paseo, int extra) { // ← Cambiado a int
    double total = 0.0;
    
    if (banda) total += PRECIO_BANDA;
    if (castillo) total += PRECIO_CASTILLO;
    if (paseo) total += PRECIO_PASEO;
    total += extra; // ← Conversión automática de int a double
    
    // Aplicar descuentos
    if ("Estudiante".equals(tipoCiudadano) || "Madre Soltera".equals(tipoCiudadano)) {
        total = total / 2; // 50% de descuento
    }
    
    return total;
}

    public String obtenerTipoCiudadano(int idCiudadano) {
        String sql = "SELECT tipo_ciudadano FROM ciudadanos WHERE id_ciudadano = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idCiudadano);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("tipo_ciudadano");
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener tipo de ciudadano: " + e.getMessage());
        }
        
        return "Común"; // Valor por defecto
    }

    private Cooperacion crearCooperacionDesdeResultSet(ResultSet rs) throws SQLException {
        Cooperacion cooperacion = new Cooperacion();
        cooperacion.setIdCooperation(rs.getInt("id_cooperation"));
        cooperacion.setAnio(rs.getInt("anio"));
        cooperacion.setBanda(rs.getDouble("banda"));
        cooperacion.setCastillo(rs.getDouble("castillo"));
        cooperacion.setPaseo(rs.getDouble("paseo"));
        cooperacion.setCooperacionExtra(rs.getInt("cooperacion_extra"));
        cooperacion.setConceptoExtra(rs.getString("concepto_extra")); 
        
        Date fecha = rs.getDate("fecha_registro");
        if (fecha != null) {
            cooperacion.setFechaRegistro(fecha.toLocalDate());
        }
        
        cooperacion.setDescuento(rs.getString("descuento"));
        cooperacion.setTotalPagado(rs.getDouble("total_pagado"));
        cooperacion.setTipoPago(rs.getString("tipo_pago"));
        cooperacion.setIdCiudadano(rs.getInt("id_ciudadano"));
        
        return cooperacion;
    }
}