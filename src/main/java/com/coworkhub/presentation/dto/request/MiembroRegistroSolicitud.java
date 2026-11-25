package com.coworkhub.presentation.dto.request;

import jakarta.validation.constraints.*;

/** Registra el miembro y su cuenta de usuario (rol MEMBER) en una sola operación. */
public record MiembroRegistroSolicitud(
        @NotBlank(message = "el documento es obligatorio") @Size(max = 20) String numeroDocumento,
        @NotBlank(message = "el nombre es obligatorio") @Size(max = 150) String nombreCompleto,
        @NotBlank(message = "el email es obligatorio") @Email(message = "el email no es válido") @Size(max = 150) String email,
        @NotNull(message = "el plan es obligatorio") Long planId,
        @NotBlank(message = "el usuario es obligatorio") @Size(min = 3, max = 50) String usuario,
        @NotBlank(message = "la contraseña es obligatoria") @Size(min = 6, max = 72) String contrasena) {
}
