package com.coworkhub.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Los filtros de seguridad actúan ANTES de llegar a los controladores, así que el @RestControllerAdvice
 * no los ve. Esta clase responde 401 y 403 con el mismo cuerpo de error que el resto de la API.
 */
@Component
public class RespuestaErrorSeguridad implements AuthenticationEntryPoint, AccessDeniedHandler {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException ex) throws IOException {
        escribir(response, 401, "Debe enviar un token válido (falta, está alterado o venció)", "NO_AUTENTICADO");
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException ex) throws IOException {
        escribir(response, 403, "No tiene permiso para realizar esta acción", "ACCESO_DENEGADO");
    }

    private void escribir(HttpServletResponse response, int estado, String mensaje, String codigo)
            throws IOException {
        response.setStatus(estado);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"error\":\"" + mensaje + "\",\"codigo\":\"" + codigo
                + "\",\"timestamp\":\"" + Instant.now().truncatedTo(ChronoUnit.SECONDS) + "\"}");
    }
}
