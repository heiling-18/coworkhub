package com.coworkhub.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.coworkhub.business.service.DisponibilidadServicio;
import com.coworkhub.presentation.dto.response.SalaRespuesta;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@RestController
@Tag(name = "Disponibilidad", description = "Salas libres de una sede en una fecha y rango horario (RF-03).")
@RequestMapping("/api/disponibilidad")
@RequiredArgsConstructor
public class DisponibilidadControlador {

    private final DisponibilidadServicio servicio;

    /** RF-03: GET /api/disponibilidad?sedeId=1&fecha=2030-06-03&horaInicio=10:00&horaFin=12:00 */
    @GetMapping
    @Operation(summary = "Salas libres (filtros opcionales: capacidadMinima y equipamientoIds)")
    public List<SalaRespuesta> salasLibres(
            @RequestParam Long sedeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime horaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime horaFin,
            @RequestParam(required = false) Integer capacidadMinima,
            @RequestParam(required = false) Set<Long> equipamientoIds) {
        return servicio.salasLibres(sedeId, fecha, horaInicio, horaFin, capacidadMinima, equipamientoIds);
    }
}
