package com.coworkhub.presentation.dto.response;

import java.math.BigDecimal;

public record PlanRespuesta(Long id, String nombre, Integer horasIncluidasMes, BigDecimal descuentoExcedente,
                            Integer maxReservasActivas) {
}
