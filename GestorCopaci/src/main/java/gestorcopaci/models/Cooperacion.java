/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gestorcopaci.models;

import java.time.LocalDate;

public class Cooperacion {
    private int idCooperation;
    private int anio;
    private double banda;
    private double castillo;
    private double paseo;
    private int cooperacionExtra;
    private LocalDate fechaRegistro;
    private String descuento;
    private double totalPagado;
    private String tipoPago;
    private int idCiudadano;

    // Constructores
    public Cooperacion() {}

    public Cooperacion(int anio, double banda, double castillo, double paseo, 
                      int cooperacionExtra, LocalDate fechaRegistro, String descuento,
                      double totalPagado, String tipoPago, int idCiudadano) {
        this.anio = anio;
        this.banda = banda;
        this.castillo = castillo;
        this.paseo = paseo;
        this.cooperacionExtra = cooperacionExtra;
        this.fechaRegistro = fechaRegistro;
        this.descuento = descuento;
        this.totalPagado = totalPagado;
        this.tipoPago = tipoPago;
        this.idCiudadano = idCiudadano;
    }

    // Getters y Setters
    public int getIdCooperation() { return idCooperation; }
    public void setIdCooperation(int idCooperation) { this.idCooperation = idCooperation; }

    public int getAnio() { return anio; }
    public void setAnio(int anio) { this.anio = anio; }

    public double getBanda() { return banda; }
    public void setBanda(double banda) { this.banda = banda; }

    public double getCastillo() { return castillo; }
    public void setCastillo(double castillo) { this.castillo = castillo; }

    public double getPaseo() { return paseo; }
    public void setPaseo(double paseo) { this.paseo = paseo; }

    public int getCooperacionExtra() { return cooperacionExtra; }
    public void setCooperacionExtra(int cooperacionExtra) { this.cooperacionExtra = cooperacionExtra; }

    public LocalDate getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDate fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public String getDescuento() { return descuento; }
    public void setDescuento(String descuento) { this.descuento = descuento; }

    public double getTotalPagado() { return totalPagado; }
    public void setTotalPagado(double totalPagado) { this.totalPagado = totalPagado; }

    public String getTipoPago() { return tipoPago; }
    public void setTipoPago(String tipoPago) { this.tipoPago = tipoPago; }

    public int getIdCiudadano() { return idCiudadano; }
    public void setIdCiudadano(int idCiudadano) { this.idCiudadano = idCiudadano; }

    @Override
    public String toString() {
        return "Cooperación " + anio + " - Total: $" + totalPagado;
    }
}