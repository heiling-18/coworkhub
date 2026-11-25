package com.coworkhub.presentation.controller;

import com.coworkhub.business.service.AuthServicio;
import com.coworkhub.presentation.dto.request.LoginSolicitud;
import com.coworkhub.presentation.dto.response.LoginRespuesta;
import com.coworkhub.presentation.dto.response.PerfilRespuesta;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Login con JWT (vence en 60 minutos) y perfil del usuario")
public class AuthControlador {

    private final AuthServicio servicio;

    @PostMapping("/login")
    @SecurityRequirements   // público: Swagger no le pone candado
    @Operation(summary = "Iniciar sesión (público). Devuelve el JWT")
    public LoginRespuesta login(@Valid @RequestBody LoginSolicitud solicitud) {
        return servicio.login(solicitud);
    }

    @GetMapping("/perfil")
    @Operation(summary = "Usuario, rol y miembroId de quien inició sesión")
    public PerfilRespuesta perfil() {
        return servicio.perfil();
    }
}
