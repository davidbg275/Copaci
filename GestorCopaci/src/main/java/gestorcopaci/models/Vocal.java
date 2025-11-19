/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gestorcopaci.models;

import java.util.Date;

public class Vocal {
    private int idVocal;
    private int idCiudadano;
    private Date fechaInicioMandato;
    private Date fechaFinMandato;
    private int eventosAsistidos;
    private double porcentajeAsistencia;
    private String estado;
    private Date createdAt;
    private Date updatedAt;
    
    // Constructor vacío
    public Vocal() {}
    
    // Constructor completo
    public Vocal(int idVocal, int idCiudadano, Date fechaInicioMandato, Date fechaFinMandato, 
                 int eventosAsistidos, double porcentajeAsistencia, String estado, 
                 Date createdAt, Date updatedAt) {
        this.idVocal = idVocal;
        this.idCiudadano = idCiudadano;
        this.fechaInicioMandato = fechaInicioMandato;
        this.fechaFinMandato = fechaFinMandato;
        this.eventosAsistidos = eventosAsistidos;
        this.porcentajeAsistencia = porcentajeAsistencia;
        this.estado = estado;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Getters y Setters
    public int getIdVocal() { return idVocal; }
    public void setIdVocal(int idVocal) { this.idVocal = idVocal; }
    
    public int getIdCiudadano() { return idCiudadano; }
    public void setIdCiudadano(int idCiudadano) { this.idCiudadano = idCiudadano; }
    
    public Date getFechaInicioMandato() { return fechaInicioMandato; }
    public void setFechaInicioMandato(Date fechaInicioMandato) { this.fechaInicioMandato = fechaInicioMandato; }
    
    public Date getFechaFinMandato() { return fechaFinMandato; }
    public void setFechaFinMandato(Date fechaFinMandato) { this.fechaFinMandato = fechaFinMandato; }
    
    public int getEventosAsistidos() { return eventosAsistidos; }
    public void setEventosAsistidos(int eventosAsistidos) { this.eventosAsistidos = eventosAsistidos; }
    
    public double getPorcentajeAsistencia() { return porcentajeAsistencia; }
    public void setPorcentajeAsistencia(double porcentajeAsistencia) { this.porcentajeAsistencia = porcentajeAsistencia; }
    
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    
    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
    
    @Override
    public String toString() {
        return "Vocal{" + "idVocal=" + idVocal + ", idCiudadano=" + idCiudadano + 
               ", eventosAsistidos=" + eventosAsistidos + ", estado=" + estado + '}';
    }
}