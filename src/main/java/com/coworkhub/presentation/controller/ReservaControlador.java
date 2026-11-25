package com.coworkhub.presentation.controller;

import com.coworkhub.business.service.ReservaServicio;
import com.coworkhub.persistence.enums.BookingStatus;
import com.coworkhub.presentation.dto.request.ReservaSolicitud;
import com.coworkhub.presentation.dto.request.ServicioSolicitud;
import com.coworkhub.presentation.dto.response.CancelacionRespuesta;
import com.coworkhub.presentation.dto.response.ReservaRespuesta;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
public class ReservaControlador {

    private final ReservaServicio servicio;

    /** RF-04: crea la reserva con servicios y devuelve el costo calculado. */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservaRespuesta crear(@Valid @RequestBody ReservaSolicitud solicitud) {
        return servicio.crear(solicitud);
    }

    @GetMapping("/{id}")
    public ReservaRespuesta obtener(@PathVariable Long id) {
        return servicio.obtener(id);
    }

    /** RF-05: ?miembroId=  ·  ?salaId=&fecha=  ·  ?estado= */
    @GetMapping
    public List<ReservaRespuesta> listar(@RequestParam(required = false) Long miembroId,
                                         @RequestParam(required = false) Long salaId,
                                         @RequestParam(required = false)
                                         @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
                                         @RequestParam(required = false) BookingStatus estado) {
        return servicio.listar(miembroId, salaId, fecha, estado);
    }

    @PostMapping("/{id}/servicios")
    public ReservaRespuesta agregarServicio(@PathVariable Long id, @Valid @RequestBody ServicioSolicitud solicitud) {
        return servicio.agregarServicio(id, solicitud);
    }

    @DeleteMapping("/{id}/servicios/{servicioId}")
    public ReservaRespuesta quitarServicio(@PathVariable Long id, @PathVariable Long servicioId) {
        return servicio.quitarServicio(id, servicioId);
    }

    @PostMapping("/{id}/confirmar")
    public ReservaRespuesta confirmar(@PathVariable Long id) {
        return servicio.confirmar(id);
    }

    @PostMapping("/{id}/cancelar")
    public CancelacionRespuesta cancelar(@PathVariable Long id) {
        return servicio.cancelar(id);
    }

    @PostMapping("/{id}/completar")
    public ReservaRespuesta completar(@PathVariable Long id) {
        return servicio.completar(id);
    }
}
