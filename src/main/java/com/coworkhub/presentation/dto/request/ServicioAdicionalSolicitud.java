package com.coworkhub.presentation.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ServicioAdicionalSolicitud(
        @NotBlank(message = "el nombre es obligatorio") @Size(max = 100) String nombre,
        @NotNull @DecimalMin(value = "0.0", message = "el precio no puede ser negativo") BigDecimal precioUnitario) {
}
