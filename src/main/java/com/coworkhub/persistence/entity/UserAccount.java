package com.coworkhub.persistence.entity;

import com.coworkhub.persistence.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Cuenta para iniciar sesion. Se llama UserAccount para no chocar con User de Spring Security. */
@Entity
@Table(name = "user_accounts")
@Getter
@Setter
@NoArgsConstructor
public class UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // RN-11: usuario unico
    @Column(nullable = false, length = 50, unique = true)
    private String username;

    // Hash BCrypt, nunca la contrasena en texto plano
    @Column(nullable = false, length = 100)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    // 1 a 1 opcional. UserAccount es la DUENIA (columna member_id UNIQUE).
    // ADMIN y RECEPTIONIST no son miembros: member_id = null.
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", unique = true)
    private Member member;
}
