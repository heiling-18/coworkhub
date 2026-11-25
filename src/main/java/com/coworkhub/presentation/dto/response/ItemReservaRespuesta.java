package com.coworkhub.presentation.dto.response;

import java.math.BigDecimal;

public record ItemReservaRespuesta(Long servicioId, String servicioNombre, Integer cantidad,
                                   BigDecimal precioUnitarioAplicado, BigDecimal subtotal) {
}
