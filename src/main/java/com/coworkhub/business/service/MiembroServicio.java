package com.coworkhub.business.service;

import com.coworkhub.persistence.enums.MemberStatus;
import com.coworkhub.presentation.dto.request.MiembroActualizacionSolicitud;
import com.coworkhub.presentation.dto.request.MiembroRegistroSolicitud;
import com.coworkhub.presentation.dto.response.ConsumoRespuesta;
import com.coworkhub.presentation.dto.response.MiembroRespuesta;

import java.time.YearMonth;
import java.util.List;

public interface MiembroServicio {

    List<MiembroRespuesta> listar(String nombre, MemberStatus estado);

    MiembroRespuesta obtener(Long id);

    MiembroRespuesta registrar(MiembroRegistroSolicitud solicitud);

    MiembroRespuesta actualizar(Long id, MiembroActualizacionSolicitud solicitud);

    MiembroRespuesta cambiarEstado(Long id, MemberStatus estado);

    void eliminar(Long id);

    ConsumoRespuesta consumo(Long id, YearMonth mes);
}
