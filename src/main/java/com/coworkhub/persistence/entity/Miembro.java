package com.coworkhub.persistence.entity;

import java.util.ArrayList;
import java.util.List;

import com.coworkhub.persistence.enums.EstadoMiembro;
import com.coworkhub.persistence.enums.Rol;

import jakarta.persistence.*;

@Entity 
@Table (name = "miembros")
public class Miembro {
@Id 
@GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
private Long id;

@Column (name = "numero_documento", nullable = false,
length = 20, unique = true)
private String numeroDocumento;

@Column (name = "nombre_completo", nullable = false, length = 150)
private String nombreCompleto;

@Column (name = "correo_electronico", nullable = false, length = 150, unique = true)
private String correoElectronico;

@ManyToOne(fetch = FetchType.LAZY, optional = false)
@JoinColumn(name = "plan_id", nullable = false)
private PlanMembresia plan;

@Enumerated(EnumType.STRING)
@Column (name = "estado", nullable = false, length = 20)
private EstadoMiembro estado = EstadoMiembro.ACTIVO;

@OneToMany(mappedBy = "miembro", fetch = FetchType.LAZY)
private List<Reserva> reservas = new ArrayList<>();

// Constructor
public Miembro() {
}
// Getters y Setters

public Long getId() {
    return id;
}
public void setId(Long id) {
    this.id = id;
}
public String getNumeroDocumento() {
    return numeroDocumento;
}
public void setNumeroDocumento(String numeroDocumento) {
    this.numeroDocumento = numeroDocumento;
}
public String getNombreCompleto() {
    return nombreCompleto;
}
public void setNombreCompleto(String nombreCompleto) {
    this.nombreCompleto = nombreCompleto;
}
public String getCorreoElectronico() {
    return correoElectronico;
}
public void setCorreoElectronico(String correoElectronico) {
    this.correoElectronico = correoElectronico;
}
public PlanMembresia getPlan() {
    return plan;
}
public void setPlan(PlanMembresia plan) {
    this.plan = plan;
}
public EstadoMiembro getEstado() {
    return estado;
}
public void setEstado(EstadoMiembro estado) {
    this.estado = estado;
}

public List<Reserva> getReservas() {
    return reservas;
}

public void setReservas(List<Reserva> reservas) {
    this.reservas = reservas;
}
}

