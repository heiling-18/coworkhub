package com.coworkhub.business.service;

import com.coworkhub.presentation.dto.request.ServicioAdicionalSolicitud;
import com.coworkhub.presentation.dto.response.ServicioAdicionalRespuesta;

import java.util.List;

public interface ServicioAdicionalServicio {

    List<ServicioAdicionalRespuesta> listar();

    ServicioAdicionalRespuesta obtener(Long id);

    ServicioAdicionalRespuesta crear(ServicioAdicionalSolicitud solicitud);

    ServicioAdicionalRespuesta actualizar(Long id, ServicioAdicionalSolicitud solicitud);

    void eliminar(Long id);
}
