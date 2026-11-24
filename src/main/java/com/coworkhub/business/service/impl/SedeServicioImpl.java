package com.coworkhub.business.service.impl;

import com.coworkhub.business.exception.NoEncontradoException;
import com.coworkhub.business.exception.ReglaNegocioException;
import com.coworkhub.business.mapper.Mapeador;
import com.coworkhub.business.service.SedeServicio;
import com.coworkhub.persistence.entity.Location;
import com.coworkhub.persistence.repository.LocationRepository;
import com.coworkhub.persistence.repository.RoomRepository;
import com.coworkhub.presentation.dto.request.SedeSolicitud;
import com.coworkhub.presentation.dto.response.SedeRespuesta;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class SedeServicioImpl implements SedeServicio {

    private final LocationRepository locationRepository;
    private final RoomRepository roomRepository;

    // Inyección por constructor: Spring pasa los repositorios solo.
    public SedeServicioImpl(LocationRepository locationRepository, RoomRepository roomRepository) {
        this.locationRepository = locationRepository;
        this.roomRepository = roomRepository;
    }

    @Override
    public List<SedeRespuesta> listar() {
        return locationRepository.findAllByOrderByIdAsc().stream()
                .map(Mapeador::aSede)
                .toList();
    }

    @Override
    public SedeRespuesta obtener(Long id) {
        return Mapeador.aSede(buscar(id));
    }

    @Override
    @Transactional
    public SedeRespuesta crear(SedeSolicitud solicitud) {
        Location sede = new Location();
        copiar(solicitud, sede);
        return Mapeador.aSede(locationRepository.save(sede));
    }

    @Override
    @Transactional
    public SedeRespuesta actualizar(Long id, SedeSolicitud solicitud) {
        Location sede = buscar(id);
        copiar(solicitud, sede);
        return Mapeador.aSede(sede);   // no hace falta save(): JPA guarda los cambios al terminar la transacción
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Location sede = buscar(id);
        // RN-12: no se borra una sede que tiene salas
        if (roomRepository.existsByLocationId(id)) {
            throw new ReglaNegocioException("RN-12", "No se puede eliminar la sede porque tiene salas");
        }
        locationRepository.delete(sede);
    }

    private void copiar(SedeSolicitud s, Location sede) {
        sede.setName(s.nombre());
        sede.setCity(s.ciudad());
        sede.setAddress(s.direccion());
        sede.setOpeningTime(s.horaApertura());
        sede.setClosingTime(s.horaCierre());
    }

    private Location buscar(Long id) {
        return locationRepository.findById(id)
                .orElseThrow(() -> new NoEncontradoException("No existe la sede " + id));
    }
}
