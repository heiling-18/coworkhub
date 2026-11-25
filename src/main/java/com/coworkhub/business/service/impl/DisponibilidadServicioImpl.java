package com.coworkhub.business.service.impl;

import com.coworkhub.business.exception.DatosInvalidosException;
import com.coworkhub.business.exception.NoEncontradoException;
import com.coworkhub.business.mapper.Mapeador;
import com.coworkhub.business.service.DisponibilidadServicio;
import com.coworkhub.persistence.entity.Equipment;
import com.coworkhub.persistence.entity.Location;
import com.coworkhub.persistence.enums.BookingStatus;
import com.coworkhub.persistence.repository.BookingRepository;
import com.coworkhub.persistence.repository.LocationRepository;
import com.coworkhub.persistence.repository.RoomRepository;
import com.coworkhub.presentation.dto.response.SalaRespuesta;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** RF-03: salas activas de una sede sin reservas que se crucen con el rango pedido. */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DisponibilidadServicioImpl implements DisponibilidadServicio {

    private final LocationRepository locationRepository;
    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;

    @Override
    public List<SalaRespuesta> salasLibres(Long sedeId, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin,
                                           Integer capacidadMinima, Set<Long> equipamientoIds) {
        if (!horaFin.isAfter(horaInicio)) {
            throw new DatosInvalidosException("horaFin debe ser posterior a horaInicio");
        }
        Location sede = locationRepository.findById(sedeId)
                .orElseThrow(() -> new NoEncontradoException("No existe la sede " + sedeId));
        if (horaInicio.isBefore(sede.getOpeningTime()) || horaFin.isAfter(sede.getClosingTime())) {
            return List.of();   // fuera del horario de la sede no hay nada disponible
        }
        Set<Long> ocupadas = new HashSet<>(bookingRepository.busyRoomIds(sedeId,
                List.of(BookingStatus.PENDING, BookingStatus.CONFIRMED),
                fecha.atTime(horaInicio), fecha.atTime(horaFin)));
        Set<Long> pedidos = equipamientoIds == null ? Set.of() : equipamientoIds;
        return roomRepository.findByLocationIdAndActiveTrueOrderByIdAsc(sedeId).stream()
                .filter(sala -> !ocupadas.contains(sala.getId()))
                .filter(sala -> capacidadMinima == null || sala.getCapacity() >= capacidadMinima)
                .filter(sala -> sala.getEquipment().stream().map(Equipment::getId).toList().containsAll(pedidos))
                .map(Mapeador::aSala)
                .toList();
    }
}
