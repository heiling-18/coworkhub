package com.coworkhub.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EquipamientoSolicitud(@NotBlank(message = "el nombre es obligatorio") @Size(max = 100) String nombre) {
}
