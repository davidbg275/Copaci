/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gestorcopaci.models;

import java.time.LocalDate;

public class Donacion {
    private int idExtra;
    private int idCiudadano;
    private String concepto;
    private double monto;
    private LocalDate fecha;

    // Constructores
    public Donacion() {}

    public Donacion(int idCiudadano, String concepto, double monto, LocalDate fecha) {
        this.idCiudadano = idCiudadano;
        this.concepto = concepto;
        this.monto = monto;
        this.fecha = fecha;
    }

    // Getters y Setters
    public int getIdExtra() { return idExtra; }
    public void setIdExtra(int idExtra) { this.idExtra = idExtra; }

    public int getIdCiudadano() { return idCiudadano; }
    public void setIdCiudadano(int idCiudadano) { this.idCiudadano = idCiudadano; }

    public String getConcepto() { return concepto; }
    public void setConcepto(String concepto) { this.concepto = concepto; }

    public double getMonto() { return monto; }
    public void setMonto(double monto) { this.monto = monto; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    @Override
    public String toString() {
        return concepto + " - $" + monto + " - " + fecha;
    }
}