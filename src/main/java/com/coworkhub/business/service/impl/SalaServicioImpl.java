package com.coworkhub.business.service.impl;

import com.coworkhub.business.exception.NoEncontradoException;
import com.coworkhub.business.exception.ReglaNegocioException;
import com.coworkhub.business.mapper.Mapeador;
import com.coworkhub.business.service.SalaServicio;
import com.coworkhub.persistence.entity.Equipment;
import com.coworkhub.persistence.entity.Location;
import com.coworkhub.persistence.entity.Room;
import com.coworkhub.persistence.repository.BookingRepository;
import com.coworkhub.persistence.repository.EquipmentRepository;
import com.coworkhub.persistence.repository.LocationRepository;
import com.coworkhub.persistence.repository.RoomRepository;
import com.coworkhub.presentation.dto.request.SalaSolicitud;
import com.coworkhub.presentation.dto.response.SalaRespuesta;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SalaServicioImpl implements SalaServicio {

    private final RoomRepository roomRepository;
    private final LocationRepository locationRepository;
    private final EquipmentRepository equipmentRepository;
    private final BookingRepository bookingRepository;

    @Override
    public List<SalaRespuesta> listar(Long sedeId) {
        List<Room> salas = sedeId == null
                ? roomRepository.findAllByOrderByIdAsc()
                : roomRepository.findByLocationIdOrderByIdAsc(sedeId);
        return salas.stream().map(Mapeador::aSala).toList();
    }

    @Override
    public SalaRespuesta obtener(Long id) {
        return Mapeador.aSala(buscar(id));
    }

    @Override
    @Transactional
    public SalaRespuesta crear(SalaSolicitud s) {
        if (roomRepository.existsByNameIgnoreCaseAndLocationId(s.nombre().trim(), s.sedeId())) {
            throw new ReglaNegocioException("RN-11", "Ya existe una sala con ese nombre en la sede");
        }
        Room sala = new Room();
        copiar(s, sala);
        return Mapeador.aSala(roomRepository.save(sala));
    }

    @Override
    @Transactional
    public SalaRespuesta actualizar(Long id, SalaSolicitud s) {
        Room sala = buscar(id);
        if (roomRepository.existsByNameIgnoreCaseAndLocationIdAndIdNot(s.nombre().trim(), s.sedeId(), id)) {
            throw new ReglaNegocioException("RN-11", "Ya existe una sala con ese nombre en la sede");
        }
        copiar(s, sala);
        return Mapeador.aSala(sala);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Room sala = buscar(id);
        if (bookingRepository.existsByRoomId(id)) {   // RN-12
            throw new ReglaNegocioException("RN-12", "No se puede eliminar la sala porque tiene reservas");
        }
        roomRepository.delete(sala);
    }

    private void copiar(SalaSolicitud s, Room sala) {
        Location sede = locationRepository.findById(s.sedeId())
                .orElseThrow(() -> new NoEncontradoException("No existe la sede " + s.sedeId()));
        Set<Long> ids = s.equipamientoIds() == null ? Set.of() : s.equipamientoIds();
        List<Equipment> equipos = equipmentRepository.findAllById(ids);
        if (equipos.size() != ids.size()) {
            throw new NoEncontradoException("Uno o más equipamientos no existen");
        }
        sala.setName(s.nombre().trim());
        sala.setType(s.tipo());
        sala.setCapacity(s.capacidad());
        sala.setHourlyRate(s.tarifaHora());
        sala.setActive(s.activa() == null || s.activa());
        sala.setLocation(sede);
        sala.setEquipment(new HashSet<>(equipos));
    }

    private Room buscar(Long id) {
        return roomRepository.findDetailById(id)
                .orElseThrow(() -> new NoEncontradoException("No existe la sala " + id));
    }
}
