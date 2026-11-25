package com.coworkhub.presentation.dto.response;

import com.coworkhub.persistence.enums.RoomType;

import java.math.BigDecimal;
import java.util.List;

public record SalaRespuesta(Long id, String nombre, RoomType tipo, Integer capacidad, BigDecimal tarifaHora,
                            Boolean activa, Long sedeId, String sedeNombre, List<EquipamientoRespuesta> equipamientos) {
}
