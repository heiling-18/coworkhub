package com.coworkhub.business.service;

import com.coworkhub.persistence.enums.BookingStatus;
import com.coworkhub.presentation.dto.request.ReservaSolicitud;
import com.coworkhub.presentation.dto.request.ServicioSolicitud;
import com.coworkhub.presentation.dto.response.CancelacionRespuesta;
import com.coworkhub.presentation.dto.response.ReservaRespuesta;

import java.time.LocalDate;
import java.util.List;

public interface ReservaServicio {

    ReservaRespuesta crear(ReservaSolicitud solicitud);

    ReservaRespuesta obtener(Long id);

    List<ReservaRespuesta> listar(Long miembroId, Long salaId, LocalDate fecha, BookingStatus estado);

    ReservaRespuesta agregarServicio(Long id, ServicioSolicitud solicitud);

    ReservaRespuesta quitarServicio(Long id, Long servicioId);

    ReservaRespuesta confirmar(Long id);

    CancelacionRespuesta cancelar(Long id);

    ReservaRespuesta completar(Long id);
}
