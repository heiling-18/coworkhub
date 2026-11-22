package com.coworkhub.persistence.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;



@Entity
@Table(name = "planes_membresia")
public class PlanMembresia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100, unique = true)
    private String nombre;

    @Column(name = "horas_incluidas_mes", nullable = false)
    private Integer horasIncluidasMes;

    @Column(name = "descuento_excedente", nullable = false, precision = 5, scale = 2)
    private BigDecimal descuentoExcedente;

    @Column(name = "max_reservas_activas", nullable = false)
    private Integer maxReservasActivas;

    // Un plan tiene muchos miembros. El dueño es Miembro (campo "plan").
    @OneToMany(mappedBy = "plan", fetch = FetchType.LAZY)
    private List<Miembro> miembros = new ArrayList<>();

    
// Constructor
    public PlanMembresia() {
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

    public Integer getHorasIncluidasMes() {
        return horasIncluidasMes;
    }

    public void setHorasIncluidasMes(Integer horasIncluidasMes) {
        this.horasIncluidasMes = horasIncluidasMes;
    }

    public BigDecimal getDescuentoExcedente() {
        return descuentoExcedente;
    }

    public void setDescuentoExcedente(BigDecimal descuentoExcedente) {
        this.descuentoExcedente = descuentoExcedente;
    }

    public Integer getMaxReservasActivas() {
        return maxReservasActivas;
    }

    public void setMaxReservasActivas(Integer maxReservasActivas) {
        this.maxReservasActivas = maxReservasActivas;
    }

    public List<Miembro> getMiembros() {
        return miembros;
    }

    public void setMiembros(List<Miembro> miembros) {
        this.miembros = miembros;
    }

    // Getters y Setters
    
}