package com.coworkhub.business.service.impl;

import com.coworkhub.business.exception.NoEncontradoException;
import com.coworkhub.business.exception.ReglaNegocioException;
import com.coworkhub.business.mapper.Mapeador;
import com.coworkhub.business.service.SedeServicio;
import com.coworkhub.persistence.entity.Sede;
import com.coworkhub.persistence.repository.SalaRepository;
import com.coworkhub.persistence.repository.SedeRepository;
import com.coworkhub.presentation.dto.request.SedeSolicitud;
import com.coworkhub.presentation.dto.response.SedeRespuesta;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class SedeServicioImpl implements SedeServicio {

    private final SedeRepository sedeRepository;
    private final SalaRepository salaRepository;

    
    public SedeServicioImpl(SedeRepository sedeRepository, SalaRepository salaRepository) {
        this.sedeRepository = sedeRepository;
        this.salaRepository = salaRepository;
    }

    @Override
    public List<SedeRespuesta> listar() {
        return sedeRepository.findAllByOrderByIdAsc().stream()
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
        Sede sede = new Sede();
        copiar(solicitud, sede);
        return Mapeador.aSede(sedeRepository.save(sede));
    }

    @Override
    @Transactional
    public SedeRespuesta actualizar(Long id, SedeSolicitud solicitud) {
        Sede sede = buscar(id);
        copiar(solicitud, sede);
        return Mapeador.aSede(sede);   // no hace falta save(): JPA guarda los cambios al terminar la transacción
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Sede sede = buscar(id);
        // RN-12: no se borra una sede que tiene salas
        if (salaRepository.existsBySedeId(id)) {
            throw new ReglaNegocioException("RN-12", "No se puede eliminar la sede porque tiene salas");
        }
        sedeRepository.delete(sede);
    }

    private void copiar(SedeSolicitud s, Sede sede) {
        sede.setNombre(s.nombre());
        sede.setCiudad(s.ciudad());
        sede.setDireccion(s.direccion());
        sede.setHoraApertura(s.horaApertura());
        sede.setHoraCierre(s.horaCierre());
    }

    private Sede buscar(Long id) {
        return sedeRepository.findById(id)
                .orElseThrow(() -> new NoEncontradoException("No existe la sede " + id));
    }
}