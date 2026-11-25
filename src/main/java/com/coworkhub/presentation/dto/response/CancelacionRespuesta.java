package com.coworkhub.presentation.dto.response;

import com.coworkhub.persistence.enums.BookingStatus;

import java.math.BigDecimal;

/** RF-08: resultado de cancelar, con el cargo aplicado según RN-08. */
public record CancelacionRespuesta(Long reservaId, BookingStatus estado, BigDecimal cargoCancelacion,
                                   boolean horasDevueltas, String mensaje) {
}
