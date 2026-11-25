package com.coworkhub.presentation.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MiembroActualizacionSolicitud(
        @NotBlank(message = "el nombre es obligatorio") @Size(max = 150) String nombreCompleto,
        @NotBlank(message = "el email es obligatorio") @Email(message = "el email no es válido") @Size(max = 150) String email,
        @NotNull(message = "el plan es obligatorio") Long planId) {
}
