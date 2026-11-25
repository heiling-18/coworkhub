package com.coworkhub.presentation.dto.request;

import com.coworkhub.persistence.enums.RoomType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.Set;

public record SalaSolicitud(
        @NotBlank(message = "el nombre es obligatorio") @Size(max = 100) String nombre,
        @NotNull(message = "el tipo es obligatorio") RoomType tipo,
        @NotNull @Min(value = 1, message = "la capacidad mínima es 1") Integer capacidad,
        @NotNull @DecimalMin(value = "0.0", message = "la tarifa no puede ser negativa") BigDecimal tarifaHora,
        Boolean activa,
        @NotNull(message = "la sede es obligatoria") Long sedeId,
        Set<Long> equipamientoIds) {
}
