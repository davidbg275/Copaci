/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gestorcopaci.models;

import java.time.LocalDate;

public class Ciudadano {
    private int idCiudadano;
    private String nombre;
    private LocalDate fechaNacimiento;
    private String domicilioActual;
    private String manzana;
    private String estadoCivil;
    private String ocupacion;
    private String tipoCiudadano;
    private LocalDate fechaAlta;
    private String tipoCertificado;
    private String lugarNacimiento;
    private String gradoEstudios;

    // Constructor vacío
    public Ciudadano() {}

    // Constructor con parámetros
    public Ciudadano(String nombre, LocalDate fechaNacimiento, String domicilioActual, 
                    String manzana, String estadoCivil, String ocupacion, String tipoCiudadano,
                    LocalDate fechaAlta, String tipoCertificado, String lugarNacimiento, 
                    String gradoEstudios) {
        this.nombre = nombre;
        this.fechaNacimiento = fechaNacimiento;
        this.domicilioActual = domicilioActual;
        this.manzana = manzana;
        this.estadoCivil = estadoCivil;
        this.ocupacion = ocupacion;
        this.tipoCiudadano = tipoCiudadano;
        this.fechaAlta = fechaAlta;
        this.tipoCertificado = tipoCertificado;
        this.lugarNacimiento = lugarNacimiento;
        this.gradoEstudios = gradoEstudios;
    }

    // Getters y Setters
    public int getIdCiudadano() { return idCiudadano; }
    public void setIdCiudadano(int idCiudadano) { this.idCiudadano = idCiudadano; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public String getDomicilioActual() { return domicilioActual; }
    public void setDomicilioActual(String domicilioActual) { this.domicilioActual = domicilioActual; }

    public String getManzana() { return manzana; }
    public void setManzana(String manzana) { this.manzana = manzana; }

    public String getEstadoCivil() { return estadoCivil; }
    public void setEstadoCivil(String estadoCivil) { this.estadoCivil = estadoCivil; }

    public String getOcupacion() { return ocupacion; }
    public void setOcupacion(String ocupacion) { this.ocupacion = ocupacion; }

    public String getTipoCiudadano() { return tipoCiudadano; }
    public void setTipoCiudadano(String tipoCiudadano) { this.tipoCiudadano = tipoCiudadano; }

    public LocalDate getFechaAlta() { return fechaAlta; }
    public void setFechaAlta(LocalDate fechaAlta) { this.fechaAlta = fechaAlta; }

    public String getTipoCertificado() { return tipoCertificado; }
    public void setTipoCertificado(String tipoCertificado) { this.tipoCertificado = tipoCertificado; }

    public String getLugarNacimiento() { return lugarNacimiento; }
    public void setLugarNacimiento(String lugarNacimiento) { this.lugarNacimiento = lugarNacimiento; }

    public String getGradoEstudios() { return gradoEstudios; }
    public void setGradoEstudios(String gradoEstudios) { this.gradoEstudios = gradoEstudios; }

    @Override
    public String toString() {
        return nombre + " - " + domicilioActual;
    }
}