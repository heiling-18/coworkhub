package com.coworkhub.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Datos para crear una reserva")
public record ReservaSolicitud(
        @Schema(example = "2") @NotNull(message = "la sala es obligatoria") Long salaId,
        @Schema(example = "1", description = "Obligatorio para ADMIN/RECEPTIONIST. Un MEMBER reserva a su nombre y puede omitirlo.")
        Long miembroId,
        @Schema(example = "2030-06-03T10:00:00") @NotNull(message = "la fecha de inicio es obligatoria") LocalDateTime inicio,
        @Schema(example = "2030-06-03T12:00:00") @NotNull(message = "la fecha de fin es obligatoria") LocalDateTime fin,
        @Schema(example = "4") @NotNull @Min(value = 1, message = "debe haber al menos 1 asistente") Integer asistentes,
        @Valid List<ServicioSolicitud> servicios) {
}
