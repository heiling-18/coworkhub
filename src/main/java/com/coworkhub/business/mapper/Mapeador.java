package com.coworkhub.business.mapper;

import com.coworkhub.persistence.entity.Location;
import com.coworkhub.presentation.dto.response.SedeRespuesta;

/** Convierte entidades en DTO de salida. La API nunca devuelve entidades. */
public final class Mapeador {

    private Mapeador() {
    }

    public static SedeRespuesta aSede(Location l) {
        return new SedeRespuesta(l.getId(), l.getName(), l.getCity(), l.getAddress(),
                l.getOpeningTime(), l.getClosingTime());
    }
}
