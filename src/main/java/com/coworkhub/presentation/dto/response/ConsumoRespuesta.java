package com.coworkhub.presentation.dto.response;

import java.math.BigDecimal;

/** RF-10: consumo mensual de horas de un miembro. */
public record ConsumoRespuesta(Long miembroId, String mes, BigDecimal horasIncluidas, BigDecimal horasUsadas,
                               BigDecimal horasRestantes, BigDecimal horasExcedentes) {
}
