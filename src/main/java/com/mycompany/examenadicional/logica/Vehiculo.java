/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.examenadicional.logica;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 *
 * @author Diana Bermeo C
 */
@Entity
public class Vehiculo  implements Serializable{
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Autogenerar el ID       
    int  idVehi;
    @Basic String Placa;
    @Basic String marca; 
    @Basic String estado; 
    
    @ManyToOne
    @JoinColumn(name = "propietario_id")
    private Propietario propietario;

    @OneToMany(mappedBy = "vehiculo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Turno> turnos = new ArrayList<>();

    public Vehiculo() {
    }

    public Vehiculo(int idVehi, String Placa, String marca, String estado, Propietario propietario) {
        this.idVehi = idVehi;
        this.Placa = Placa;
        this.marca = marca;
        this.estado = estado;
        this.propietario = propietario;
    }

    public int getIdVehi() {
        return idVehi;
    }

    public String getPlaca() {
        return Placa;
    }

    public String getMarca() {
        return marca;
    }

    public String getEstado() {
        return estado;
    }

    public Propietario getPropietario() {
        return propietario;
    }

    public List<Turno> getTurnos() {
        return turnos;
    }

    public void setIdVehi(int idVehi) {
        this.idVehi = idVehi;
    }

    public void setPlaca(String Placa) {
        this.Placa = Placa;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public void setPropietario(Propietario propietario) {
        this.propietario = propietario;
    }

    public void setTurnos(List<Turno> turnos) {
        this.turnos = turnos;
    }

    @Override
    public String toString() {
        return "Vehiculo{" + "idVehi=" + idVehi + ", Placa=" + Placa + ", marca=" + marca + ", estado=" + estado + ", propietario=" + propietario + ", turnos=" + turnos + '}';
    }

    
}
