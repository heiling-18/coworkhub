package com.coworkhub.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

/** Registra el miembro y su cuenta de usuario (rol MEMBER) en una sola operación. */
public record MiembroRegistroSolicitud(
        @Schema(example = "6060606060") @NotBlank(message = "el documento es obligatorio") @Size(max = 20) String numeroDocumento,
        @Schema(example = "Carla Ríos") @NotBlank(message = "el nombre es obligatorio") @Size(max = 150) String nombreCompleto,
        @Schema(example = "carla.rios@correo.com") @NotBlank(message = "el email es obligatorio") @Email(message = "el email no es válido") @Size(max = 150) String email,
        @Schema(example = "2") @NotNull(message = "el plan es obligatorio") Long planId,
        @Schema(example = "carla") @NotBlank(message = "el usuario es obligatorio") @Size(min = 3, max = 50) String usuario,
        @Schema(example = "clave123") @NotBlank(message = "la contraseña es obligatoria") @Size(min = 6, max = 72) String contrasena) {
}
