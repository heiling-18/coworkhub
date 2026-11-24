package com.coworkhub.persistence.entity;

import com.coworkhub.persistence.enums.MemberStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/** Miembro: cliente del coworking con un plan. */
@Entity
@Table(name = "members")
@Getter
@Setter
@NoArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // RN-11: documento unico
    @Column(name = "document_number", nullable = false, length = 20, unique = true)
    private String documentNumber;

    @Column(name = "full_name", nullable = false, length = 150)
    private String fullName;

    // RN-11: email unico
    @Column(nullable = false, length = 150, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MemberStatus status = MemberStatus.ACTIVE;

    // N a 1: Member es el DUENIO (columna plan_id)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "plan_id", nullable = false)
    private MembershipPlan plan;

    // 1 a N. El lado duenio es Booking (columna member_id).
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    private List<Booking> bookings = new ArrayList<>();

    // La relacion 1 a 1 con UserAccount es unidireccional desde UserAccount:
    // el lado inverso de un OneToOne no puede ser LAZY y causaria N+1 al listar miembros.
}
