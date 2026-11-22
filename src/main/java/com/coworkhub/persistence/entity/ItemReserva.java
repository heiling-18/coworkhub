package com.coworkhub.persistence.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;


@Entity 
@Table (name = "items_reserva")
public class ItemReserva {
@Id 
@GeneratedValue (strategy = jakarta.persistence.GenerationType.IDENTITY)
private Long id;

@Column(nullable = false)
private Integer cantidad;

@Column(name = "precio_unitario_aplicado", nullable = false, precision = 10, scale = 2)
private BigDecimal precioUnitarioAplicado;

@ManyToOne(fetch = FetchType.LAZY, optional = false)
@JoinColumn(name = "reserva_id", nullable = false)
private Reserva reserva;

@ManyToOne(fetch = FetchType.LAZY, optional = false)
@JoinColumn(name = "servicio_id", nullable = false)
private ServicioAdicional servicio;

//constructor
public ItemReserva() {
}
//getters y setters

public Long getId() {
    return id;
}

public BigDecimal getPrecioUnitarioAplicado() {
    return precioUnitarioAplicado;
}

public void setPrecioUnitarioAplicado(BigDecimal precioUnitarioAplicado) {
    this.precioUnitarioAplicado = precioUnitarioAplicado;
}

public ServicioAdicional getServicio() {
    return servicio;
}

public void setServicio(ServicioAdicional servicio) {
    this.servicio = servicio;
}

public void setId(Long id) {
    this.id = id;
}

public Integer getCantidad() {
    return cantidad;
}

public void setCantidad(Integer cantidad) {
    this.cantidad = cantidad;
}

public Reserva getReserva() {
    return reserva;
}

public void setReserva(Reserva reserva) {
    this.reserva = reserva;
}

}
