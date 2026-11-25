package com.coworkhub.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record LoginSolicitud(
        @Schema(example = "admin") @NotBlank(message = "el usuario es obligatorio") String usuario,
        @Schema(example = "clave123") @NotBlank(message = "la contraseña es obligatoria") String contrasena) {
}
