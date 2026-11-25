package com.coworkhub.business.service;

import com.coworkhub.presentation.dto.request.PlanSolicitud;
import com.coworkhub.presentation.dto.response.PlanRespuesta;

import java.util.List;

public interface PlanServicio {

    List<PlanRespuesta> listar();

    PlanRespuesta obtener(Long id);

    PlanRespuesta crear(PlanSolicitud solicitud);

    PlanRespuesta actualizar(Long id, PlanSolicitud solicitud);

    void eliminar(Long id);
}
