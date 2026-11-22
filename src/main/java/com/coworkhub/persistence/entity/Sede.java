package com.coworkhub.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sedes")
public class Sede {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 100)
    private String ciudad;

    @Column(nullable = false, length = 150)
    private String direccion;

    @Column(name = "hora_apertura", nullable = false)
    private LocalTime horaApertura;

    @Column(name = "hora_cierre", nullable = false)
    private LocalTime horaCierre;

    // Una sede tiene muchas salas. El dueño de la relación es Sala.
    @OneToMany(mappedBy = "sede", fetch = FetchType.LAZY)
    private List<Sala> salas = new ArrayList<>();

    
    // Constructor
    public Sede() {
    }
    // getters y setters


    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public String getNombre() {
        return nombre;
    }


    public void setNombre(String nombre) {
        this.nombre = nombre;
    }


    public String getCiudad() {
        return ciudad;
    }


    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }


    public String getDireccion() {
        return direccion;
    }


    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }


    public LocalTime getHoraApertura() {
        return horaApertura;
    }


    public void setHoraApertura(LocalTime horaApertura) {
        this.horaApertura = horaApertura;
    }


    public LocalTime getHoraCierre() {
        return horaCierre;
    }


    public void setHoraCierre(LocalTime horaCierre) {
        this.horaCierre = horaCierre;
    }


    public List<Sala> getSalas() {
        return salas;
    }


    public void setSalas(List<Sala> salas) {
        this.salas = salas;
    }
    
}