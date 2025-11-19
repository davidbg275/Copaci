/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gestorcopaci.models;

import java.util.Date;

public class Evento {
    private int idEvento;
    private String nombreEvento;
    private Date fechaEvento;
    private int valorEvento;
    private String estadoEvento;
    private Date createdAt;
    
    // Constructor vacío
    public Evento() {}
    
    // Constructor completo
    public Evento(int idEvento, String nombreEvento, Date fechaEvento, int valorEvento, 
                  String estadoEvento, Date createdAt) {
        this.idEvento = idEvento;
        this.nombreEvento = nombreEvento;
        this.fechaEvento = fechaEvento;
        this.valorEvento = valorEvento;
        this.estadoEvento = estadoEvento;
        this.createdAt = createdAt;
    }
    
    // Getters y Setters
    public int getIdEvento() { return idEvento; }
    public void setIdEvento(int idEvento) { this.idEvento = idEvento; }
    
    public String getNombreEvento() { return nombreEvento; }
    public void setNombreEvento(String nombreEvento) { this.nombreEvento = nombreEvento; }
    
    public Date getFechaEvento() { return fechaEvento; }
    public void setFechaEvento(Date fechaEvento) { this.fechaEvento = fechaEvento; }
    
    public int getValorEvento() { return valorEvento; }
    public void setValorEvento(int valorEvento) { this.valorEvento = valorEvento; }
    
    public String getEstadoEvento() { return estadoEvento; }
    public void setEstadoEvento(String estadoEvento) { this.estadoEvento = estadoEvento; }
    
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    
    @Override
    public String toString() {
        return nombreEvento + " (" + fechaEvento + ")";
    }
}