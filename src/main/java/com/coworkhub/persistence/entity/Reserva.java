package com.coworkhub.persistence.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

import com.coworkhub.persistence.enums.EstadoReserva;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.*;
import java.util.List;

@Entity 
@Table (name = "reservas")
public class Reserva {
    @Id 
    @GeneratedValue (strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @Column (name = "fecha_hora_inicio", nullable = false)
    private java.time.LocalDateTime fechaHoraInicio;

    @Column (nullable = false)
    private Integer asistentes;

    @Enumerated(EnumType.STRING)
    @Column (nullable = false, length = 20)
    private EstadoReserva estado = EstadoReserva.PENDIENTE;

    @Column(name = "costo_sala", nullable = false, precision = 10, scale = 2)
    private BigDecimal costoSala = BigDecimal.ZERO;

        @Column(name = "fecha_hora_fin", nullable = false)
    private LocalDateTime fechaHoraFin;

    @Column(name = "costo_servicios", nullable = false, precision = 10, scale = 2)
    private BigDecimal costoServicios = BigDecimal.ZERO;

    @Column(name = "costo_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal costoTotal = BigDecimal.ZERO;

    @Column(name = "cargo_cancelacion", nullable = false, precision = 10, scale = 2)
    private BigDecimal cargoCancelacion = BigDecimal.ZERO;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sala_id", nullable = false)
    private Sala sala;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "miembro_id", nullable = false)
    private Miembro miembro;

     @OneToMany(mappedBy = "reserva", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemReserva> items = new ArrayList<>();

    public Reserva() {
    }

     public void agregarItem(ItemReserva item) {
        items.add(item);
        item.setReserva(this);
    }

    public void quitarItem(ItemReserva item) {
        items.remove(item);
        item.setReserva(null);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public java.time.LocalDateTime getFechaHoraInicio() {
        return fechaHoraInicio;
    }

    public void setFechaHoraInicio(java.time.LocalDateTime fechaHoraInicio) {
        this.fechaHoraInicio = fechaHoraInicio;
    }

    public Integer getAsistentes() {
        return asistentes;
    }

    public void setAsistentes(Integer asistentes) {
        this.asistentes = asistentes;
    }

    public EstadoReserva getEstado() {
        return estado;
    }

    public void setEstado(EstadoReserva estado) {
        this.estado = estado;
    }

    public BigDecimal getCostoSala() {
        return costoSala;
    }

    public void setCostoSala(BigDecimal costoSala) {
        this.costoSala = costoSala;
    }

    public LocalDateTime getFechaHoraFin() {
        return fechaHoraFin;
    }

    public void setFechaHoraFin(LocalDateTime fechaHoraFin) {
        this.fechaHoraFin = fechaHoraFin;
    }

    public BigDecimal getCostoServicios() {
        return costoServicios;
    }

    public void setCostoServicios(BigDecimal costoServicios) {
        this.costoServicios = costoServicios;
    }

    public BigDecimal getCostoTotal() {
        return costoTotal;
    }

    public void setCostoTotal(BigDecimal costoTotal) {
        this.costoTotal = costoTotal;
    }

    public BigDecimal getCargoCancelacion() {
        return cargoCancelacion;
    }

    public void setCargoCancelacion(BigDecimal cargoCancelacion) {
        this.cargoCancelacion = cargoCancelacion;
    }

    public Sala getSala() {
        return sala;
    }

    public void setSala(Sala sala) {
        this.sala = sala;
    }

    public Miembro getMiembro() {
        return miembro;
    }

    public void setMiembro(Miembro miembro) {
        this.miembro = miembro;
    }

    public List<ItemReserva> getItems() {
        return items;
    }

    public void setItems(List<ItemReserva> items) {
        this.items = items;
    }
    
    //Getters y Setters

    

}
