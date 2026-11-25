package com.coworkhub.business.service;

import com.coworkhub.presentation.dto.request.EquipamientoSolicitud;
import com.coworkhub.presentation.dto.response.EquipamientoRespuesta;

import java.util.List;

public interface EquipamientoServicio {

    List<EquipamientoRespuesta> listar();

    EquipamientoRespuesta obtener(Long id);

    EquipamientoRespuesta crear(EquipamientoSolicitud solicitud);

    EquipamientoRespuesta actualizar(Long id, EquipamientoSolicitud solicitud);

    void eliminar(Long id);
}
