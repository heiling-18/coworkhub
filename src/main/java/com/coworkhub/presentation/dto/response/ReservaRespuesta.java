package com.coworkhub.presentation.dto.response;

import com.coworkhub.persistence.enums.BookingStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ReservaRespuesta(Long id, Long salaId, String salaNombre, Long miembroId, String miembroNombre,
                               LocalDateTime inicio, LocalDateTime fin, Integer asistentes, BookingStatus estado,
                               BigDecimal costoSala, BigDecimal costoServicios, BigDecimal costoTotal,
                               BigDecimal cargoCancelacion, List<ItemReservaRespuesta> servicios) {
}
