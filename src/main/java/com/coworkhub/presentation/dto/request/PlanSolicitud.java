package com.coworkhub.presentation.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record PlanSolicitud(
        @NotBlank(message = "el nombre es obligatorio") @Size(max = 100) String nombre,
        @NotNull @Min(0) Integer horasIncluidasMes,
        @NotNull @DecimalMin("0.0") @DecimalMax("100.0") BigDecimal descuentoExcedente,
        @NotNull @Min(1) Integer maxReservasActivas) {
}
