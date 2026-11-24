package com.coworkhub.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/** Servicio adicional que se agrega a una reserva (cafe, refrigerio...). */
@Entity
@Table(name = "additional_services")
@Getter
@Setter
@NoArgsConstructor
public class AdditionalService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // RN-11: nombre unico
    @Column(nullable = false, length = 100, unique = true)
    private String name;

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;
}
