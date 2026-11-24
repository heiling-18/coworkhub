package com.coworkhub.business.mapper;

import com.coworkhub.persistence.entity.Sede;
import com.coworkhub.presentation.dto.response.SedeRespuesta;

/** Convierte entidades en DTO de salida. */
public final class Mapeador {

    private Mapeador() {
    }

    public static SedeRespuesta aSede(Sede s) {
        return new SedeRespuesta(s.getId(), s.getNombre(), s.getCiudad(), s.getDireccion(),
                s.getHoraApertura(), s.getHoraCierre());
    }
}