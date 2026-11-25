package com.coworkhub.business.service;

import com.coworkhub.presentation.dto.request.SalaSolicitud;
import com.coworkhub.presentation.dto.response.SalaRespuesta;

import java.util.List;

public interface SalaServicio {

    List<SalaRespuesta> listar(Long sedeId);

    SalaRespuesta obtener(Long id);

    SalaRespuesta crear(SalaSolicitud solicitud);

    SalaRespuesta actualizar(Long id, SalaSolicitud solicitud);

    void eliminar(Long id);
}
