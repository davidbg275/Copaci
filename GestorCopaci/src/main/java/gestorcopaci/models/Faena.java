package gestorcopaci.models;

import java.time.LocalDate;

public class Faena {

    private int idFaena;
    private int anio;
    private String mes;
    private boolean asistencia;      // true = asistió, false = NO asistió (paga reposición)
    private double pagoReposicion;   // 0, 50 o 100 según tipo
    private LocalDate fechaRegistro;
    private int idCiudadano;

    public Faena() {
    }

    public Faena(int anio, String mes, boolean asistencia,
                 double pagoReposicion, LocalDate fechaRegistro, int idCiudadano) {
        this.anio = anio;
        this.mes = mes;
        this.asistencia = asistencia;
        this.pagoReposicion = pagoReposicion;
        this.fechaRegistro = fechaRegistro;
        this.idCiudadano = idCiudadano;
    }

    public int getIdFaena() {
        return idFaena;
    }

    public void setIdFaena(int idFaena) {
        this.idFaena = idFaena;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public boolean isAsistencia() {
        return asistencia;
    }

    public void setAsistencia(boolean asistencia) {
        this.asistencia = asistencia;
    }

    public double getPagoReposicion() {
        return pagoReposicion;
    }

    public void setPagoReposicion(double pagoReposicion) {
        this.pagoReposicion = pagoReposicion;
    }

    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDate fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public int getIdCiudadano() {
        return idCiudadano;
    }

    public void setIdCiudadano(int idCiudadano) {
        this.idCiudadano = idCiudadano;
    }

    @Override
    public String toString() {
        return "Faena " + anio + " - " + mes +
               " | asistencia=" + (asistencia ? "Sí" : "No") +
               " | pago=" + pagoReposicion;
    }
}
