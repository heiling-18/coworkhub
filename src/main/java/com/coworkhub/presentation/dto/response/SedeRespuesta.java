
package com.coworkhub.presentation.dto.response;

import java.time.LocalTime;

public record SedeRespuesta(Long id, String nombre, String ciudad, String direccion,
                            LocalTime horaApertura, LocalTime horaCierre) {
}