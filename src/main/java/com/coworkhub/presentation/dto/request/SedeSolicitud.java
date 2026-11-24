package com.coworkhub.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalTime;

public record SedeSolicitud(
        @NotBlank(message = "el nombre es obligatorio") @Size(max = 100) String nombre,
        @NotBlank(message = "la ciudad es obligatoria") String ciudad,
        @NotBlank(message = "la dirección es obligatoria") String direccion,
        @NotNull LocalTime horaApertura,
        @NotNull LocalTime horaCierre) {
}