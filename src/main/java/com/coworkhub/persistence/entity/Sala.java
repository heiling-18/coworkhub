package com.coworkhub.persistence.entity;

import com.coworkhub.persistence.enums.TipoSala;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "salas",
       //el nombre de la sala no se repite dentro de la misma sede
       uniqueConstraints = @UniqueConstraint(columnNames = {"nombre", "sede_id"}))
public class Sala {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoSala tipo;

    @Column(name = "tarifa_hora", nullable = false, precision = 10, scale = 2)
    private BigDecimal tarifaHora;

    @Column(nullable = false)
    private Integer capacidad;

    @Column(nullable = false)
    private Boolean activa = true;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sede_id", nullable = false)
    private Sede sede;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "sala_equipamiento",
               joinColumns = @JoinColumn(name = "sala_id"),
               inverseJoinColumns = @JoinColumn(name = "equipamiento_id"))
    private Set<Equipamiento> equipamientos = new HashSet<>();

    
    // Constructor
    public Sala() {
    }   
    // Getters y Setters


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


    public TipoSala getTipo() {
        return tipo;
    }


    public void setTipo(TipoSala tipo) {
        this.tipo = tipo;
    }


    public BigDecimal getTarifaHora() {
        return tarifaHora;
    }


    public void setTarifaHora(BigDecimal tarifaHora) {
        this.tarifaHora = tarifaHora;
    }


    public Integer getCapacidad() {
        return capacidad;
    }


    public void setCapacidad(Integer capacidad) {
        this.capacidad = capacidad;
    }


    public Boolean getActiva() {
        return activa;
    }


    public void setActiva(Boolean activa) {
        this.activa = activa;
    }


    public Sede getSede() {
        return sede;
    }


    public void setSede(Sede sede) {
        this.sede = sede;
    }


    public Set<Equipamiento> getEquipamientos() {
        return equipamientos;
    }


    public void setEquipamientos(Set<Equipamiento> equipamientos) {
        this.equipamientos = equipamientos;
    }
    

}