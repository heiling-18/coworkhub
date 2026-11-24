package com.coworkhub.business.service;

import com.coworkhub.presentation.dto.request.SedeSolicitud;
import com.coworkhub.presentation.dto.response.SedeRespuesta;

import java.util.List;

public interface SedeServicio {
    List<SedeRespuesta> listar();
    SedeRespuesta obtener(Long id);
    SedeRespuesta crear(SedeSolicitud solicitud);
    SedeRespuesta actualizar(Long id, SedeSolicitud solicitud);
    void eliminar(Long id);

}
