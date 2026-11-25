package com.coworkhub.presentation.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public record ReservaSolicitud(
        @NotNull(message = "la sala es obligatoria") Long salaId,
        Long miembroId,   // obligatorio para ADMIN/RECEPTIONIST; un MEMBER reserva a su nombre
        @NotNull(message = "la fecha de inicio es obligatoria") LocalDateTime inicio,
        @NotNull(message = "la fecha de fin es obligatoria") LocalDateTime fin,
        @NotNull @Min(value = 1, message = "debe haber al menos 1 asistente") Integer asistentes,
        @Valid List<ServicioSolicitud> servicios) {
}
