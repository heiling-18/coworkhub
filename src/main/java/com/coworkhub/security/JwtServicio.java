package com.coworkhub.security;

import com.coworkhub.persistence.entity.UserAccount;
import com.coworkhub.persistence.enums.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * Genera y valida JWT firmados con HMAC-SHA. Vencen a los 60 minutos (RF-11).
 * Usa la hora real (no el reloj del negocio) para que el token no venza mal si se fija la hora en pruebas.
 */
@Service
public class JwtServicio {

    private final String secreto;
    private final long minutosExpiracion;
    private SecretKey clave;

    public JwtServicio(@Value("${app.jwt.secreto}") String secreto,
                       @Value("${app.jwt.expiracion-minutos}") long minutosExpiracion) {
        this.secreto = secreto;
        this.minutosExpiracion = minutosExpiracion;
    }

    /** Módulo 02: @PostConstruct que valida la configuración al arrancar. */
    @PostConstruct
    void validarConfiguracion() {
        if (secreto == null || secreto.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException("app.jwt.secreto debe tener al menos 32 caracteres");
        }
        if (minutosExpiracion <= 0) {
            throw new IllegalStateException("app.jwt.expiracion-minutos debe ser mayor que 0");
        }
        this.clave = Keys.hmacShaKeyFor(secreto.getBytes(StandardCharsets.UTF_8));
    }

    public String generar(UserAccount cuenta) {
        Instant ahora = Instant.now();
        JwtBuilder builder = Jwts.builder()
                .subject(cuenta.getUsername())
                .claim("rol", cuenta.getRole().name())
                .issuedAt(Date.from(ahora))
                .expiration(Date.from(ahora.plus(minutosExpiracion, ChronoUnit.MINUTES)));
        if (cuenta.getMember() != null) {
            builder.claim("miembroId", cuenta.getMember().getId());
        }
        return builder.signWith(clave).compact();
    }

    /** Lanza JwtException si el token está alterado o vencido. */
    public UsuarioAutenticado validar(String token) {
        Claims claims = Jwts.parser().verifyWith(clave).build().parseSignedClaims(token).getPayload();
        Object miembroId = claims.get("miembroId");
        return new UsuarioAutenticado(claims.getSubject(), Role.valueOf(claims.get("rol", String.class)),
                miembroId == null ? null : ((Number) miembroId).longValue());
    }

    public long getMinutosExpiracion() {
        return minutosExpiracion;
    }
}
