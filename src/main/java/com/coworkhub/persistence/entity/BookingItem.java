package com.coworkhub.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/** Entidad intermedia Booking - AdditionalService con datos propios (cantidad y precio aplicado). */
@Entity
@Table(name = "booking_items")
@Getter
@Setter
@NoArgsConstructor
public class BookingItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer quantity;

    // Precio copiado al momento de agregar el servicio (RN-07)
    @Column(name = "applied_unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal appliedUnitPrice;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "service_id", nullable = false)
    private AdditionalService service;

    public BigDecimal subtotal() {
        return appliedUnitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
