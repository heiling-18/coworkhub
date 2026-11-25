package com.coworkhub.business.service;

import com.coworkhub.presentation.dto.response.SalaRespuesta;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

public interface DisponibilidadServicio {

    List<SalaRespuesta> salasLibres(Long sedeId, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin,
                                    Integer capacidadMinima, Set<Long> equipamientoIds);
}
