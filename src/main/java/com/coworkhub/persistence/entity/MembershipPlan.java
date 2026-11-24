package com.coworkhub.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/** Plan de membresia. */
@Entity
@Table(name = "membership_plans")
@Getter
@Setter
@NoArgsConstructor
public class MembershipPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // RN-11: nombre unico
    @Column(nullable = false, length = 100, unique = true)
    private String name;

    @Column(name = "included_hours_per_month", nullable = false)
    private Integer includedHoursPerMonth;

    // Porcentaje (ej. 10.00 = 10 %) de descuento sobre las horas excedentes
    @Column(name = "overage_discount", nullable = false, precision = 5, scale = 2)
    private BigDecimal overageDiscount;

    @Column(name = "max_active_bookings", nullable = false)
    private Integer maxActiveBookings;

    // 1 a N. El lado duenio es Member (columna plan_id).
    @OneToMany(mappedBy = "plan", fetch = FetchType.LAZY)
    private List<Member> members = new ArrayList<>();
}
