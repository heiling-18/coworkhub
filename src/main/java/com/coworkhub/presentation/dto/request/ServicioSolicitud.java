package com.coworkhub.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/** Un servicio adicional dentro de una reserva. */
public record ServicioSolicitud(
        @Schema(example = "1") @NotNull(message = "el servicio es obligatorio") Long servicioId,
        @Schema(example = "2") @NotNull @Min(value = 1, message = "la cantidad mínima es 1") Integer cantidad) {
}
