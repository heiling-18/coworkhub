package com.coworkhub.presentation.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/** Un servicio adicional dentro de una reserva. */
public record ServicioSolicitud(
        @NotNull(message = "el servicio es obligatorio") Long servicioId,
        @NotNull @Min(value = 1, message = "la cantidad mínima es 1") Integer cantidad) {
}
