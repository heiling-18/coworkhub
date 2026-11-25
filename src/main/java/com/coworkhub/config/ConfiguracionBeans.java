package com.coworkhub.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;

/** Módulo 02: beans propios declarados con @Bean. */
@Configuration
public class ConfiguracionBeans {

    /**
     * Reloj del negocio. Los servicios usan LocalDateTime.now(reloj) en vez de LocalDateTime.now().
     * Si app.reloj.fijo tiene valor (ej. 2030-06-03T08:00:00) la hora queda fija: sirve para probar RN-03 y RN-08.
     */
    @Bean(name = "relojNegocio")
    public Clock relojNegocio(@Value("${app.zona-horaria}") String zonaHoraria,
                              @Value("${app.reloj.fijo:}") String relojFijo) {
        ZoneId zona = ZoneId.of(zonaHoraria);
        if (relojFijo != null && !relojFijo.isBlank()) {
            return Clock.fixed(LocalDateTime.parse(relojFijo).atZone(zona).toInstant(), zona);
        }
        return Clock.system(zona);
    }

    /** BCrypt para guardar las contraseñas con hash. */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
