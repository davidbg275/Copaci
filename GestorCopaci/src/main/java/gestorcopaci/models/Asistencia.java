/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gestorcopaci.models;

import java.util.Date;

public class Asistencia {
    private int idAsistencia;
    private int idVocal;
    private int idEvento;
    private Date fechaAsistencia;
    private Date createdAt;
    
    // Constructor vacío
    public Asistencia() {}
    
    // Constructor completo
    public Asistencia(int idAsistencia, int idVocal, int idEvento, Date fechaAsistencia, Date createdAt) {
        this.idAsistencia = idAsistencia;
        this.idVocal = idVocal;
        this.idEvento = idEvento;
        this.fechaAsistencia = fechaAsistencia;
        this.createdAt = createdAt;
    }
    
    // Getters y Setters
    public int getIdAsistencia() { return idAsistencia; }
    public void setIdAsistencia(int idAsistencia) { this.idAsistencia = idAsistencia; }
    
    public int getIdVocal() { return idVocal; }
    public void setIdVocal(int idVocal) { this.idVocal = idVocal; }
    
    public int getIdEvento() { return idEvento; }
    public void setIdEvento(int idEvento) { this.idEvento = idEvento; }
    
    public Date getFechaAsistencia() { return fechaAsistencia; }
    public void setFechaAsistencia(Date fechaAsistencia) { this.fechaAsistencia = fechaAsistencia; }
    
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    
    @Override
    public String toString() {
        return "Asistencia{" + "idAsistencia=" + idAsistencia + ", idVocal=" + idVocal + 
               ", idEvento=" + idEvento + ", fechaAsistencia=" + fechaAsistencia + '}';
    }
}