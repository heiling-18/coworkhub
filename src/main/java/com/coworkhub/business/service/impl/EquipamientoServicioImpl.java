package com.coworkhub.business.service.impl;

import com.coworkhub.business.exception.NoEncontradoException;
import com.coworkhub.business.exception.ReglaNegocioException;
import com.coworkhub.business.mapper.Mapeador;
import com.coworkhub.business.service.EquipamientoServicio;
import com.coworkhub.persistence.entity.Equipment;
import com.coworkhub.persistence.repository.EquipmentRepository;
import com.coworkhub.persistence.repository.RoomRepository;
import com.coworkhub.presentation.dto.request.EquipamientoSolicitud;
import com.coworkhub.presentation.dto.response.EquipamientoRespuesta;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor   // Lombok genera el constructor: inyección por constructor
@Transactional(readOnly = true)
public class EquipamientoServicioImpl implements EquipamientoServicio {

    private final EquipmentRepository equipmentRepository;
    private final RoomRepository roomRepository;

    @Override
    public List<EquipamientoRespuesta> listar() {
        return equipmentRepository.findAllByOrderByIdAsc().stream().map(Mapeador::aEquipamiento).toList();
    }

    @Override
    public EquipamientoRespuesta obtener(Long id) {
        return Mapeador.aEquipamiento(buscar(id));
    }

    @Override
    @Transactional
    public EquipamientoRespuesta crear(EquipamientoSolicitud s) {
        if (equipmentRepository.existsByNameIgnoreCase(s.nombre().trim())) {
            throw new ReglaNegocioException("RN-11", "Ya existe un equipamiento con ese nombre");
        }
        Equipment equipo = new Equipment();
        equipo.setName(s.nombre().trim());
        return Mapeador.aEquipamiento(equipmentRepository.save(equipo));
    }

    @Override
    @Transactional
    public EquipamientoRespuesta actualizar(Long id, EquipamientoSolicitud s) {
        Equipment equipo = buscar(id);
        if (equipmentRepository.existsByNameIgnoreCaseAndIdNot(s.nombre().trim(), id)) {
            throw new ReglaNegocioException("RN-11", "Ya existe un equipamiento con ese nombre");
        }
        equipo.setName(s.nombre().trim());
        return Mapeador.aEquipamiento(equipo);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Equipment equipo = buscar(id);
        if (roomRepository.existsByEquipmentId(id)) {   // RN-12
            throw new ReglaNegocioException("RN-12", "No se puede eliminar el equipamiento porque lo usa una sala");
        }
        equipmentRepository.delete(equipo);
    }

    private Equipment buscar(Long id) {
        return equipmentRepository.findById(id)
                .orElseThrow(() -> new NoEncontradoException("No existe el equipamiento " + id));
    }
}
