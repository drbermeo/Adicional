/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.examenadicional.logica;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

/**
 *
 * @author Diana Bermeo C
 */
@Entity
public class Propietario  implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idPro;
    @Basic
    private String cedula;
    private String apellido;
    private String nombre;
    
    @OneToMany(mappedBy = "Propietario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Vehiculo> vehiculos = new ArrayList<>();

    public Propietario() {
    }

    public int getIdPro() {
        return idPro;
    }

    public String getCedula() {
        return cedula;
    }

    public String getApellido() {
        return apellido;
    }

    public String getNombre() {
        return nombre;
    }

    public List<Vehiculo> getVehiculos() {
        return vehiculos;
    }

    public void setIdPro(int idPro) {
        this.idPro = idPro;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setVehiculos(List<Vehiculo> vehiculos) { this.vehiculos = vehiculos; }
    
}
