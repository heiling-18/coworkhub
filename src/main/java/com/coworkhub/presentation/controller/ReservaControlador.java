package com.coworkhub.presentation.controller;

import org.springframework.security.access.prepost.PreAuthorize;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

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
@Tag(name = "Reservas", description = "Crear, consultar, cambiar servicios, confirmar, cancelar y completar reservas.")
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
public class ReservaControlador {

    private final ReservaServicio servicio;

    /** RF-04: crea la reserva con servicios y devuelve el costo calculado. */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear reserva con servicios; devuelve el costo calculado (RN-01 a RN-07)")
    public ReservaRespuesta crear(@Valid @RequestBody ReservaSolicitud solicitud) {
        return servicio.crear(solicitud);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar reserva (MEMBER solo si es suya)")
    public ReservaRespuesta obtener(@PathVariable Long id) {
        return servicio.obtener(id);
    }

    /** RF-05: ?miembroId=  ·  ?salaId=&fecha=  ·  ?estado= */
    @GetMapping
    @Operation(summary = "Listar por ?miembroId=, por ?salaId=    public List<ReservaRespuesta> listar(fecha= o por ?estado=")
    public List<ReservaRespuesta> listar(@RequestParam(required = false) Long miembroId,
                                         @RequestParam(required = false) Long salaId,
                                         @RequestParam(required = false)
                                         @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
                                         @RequestParam(required = false) BookingStatus estado) {
        return servicio.listar(miembroId, salaId, fecha, estado);
    }

    @PostMapping("/{id}/servicios")
    @Operation(summary = "Agregar servicio a una reserva PENDING y recalcular (RN-10)")
    public ReservaRespuesta agregarServicio(@PathVariable Long id, @Valid @RequestBody ServicioSolicitud solicitud) {
        return servicio.agregarServicio(id, solicitud);
    }

    @DeleteMapping("/{id}/servicios/{servicioId}")
    @Operation(summary = "Quitar servicio de una reserva PENDING y recalcular (RN-10)")
    public ReservaRespuesta quitarServicio(@PathVariable Long id, @PathVariable Long servicioId) {
        return servicio.quitarServicio(id, servicioId);
    }

    @PostMapping("/{id}/confirmar")
    @Operation(summary = "Confirmar: PENDING -> CONFIRMED (RN-09)")
    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    public ReservaRespuesta confirmar(@PathVariable Long id) {
        return servicio.confirmar(id);
    }

    @PostMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar aplicando RN-08 y devolver el cargo")
    public CancelacionRespuesta cancelar(@PathVariable Long id) {
        return servicio.cancelar(id);
    }

    @PostMapping("/{id}/completar")
    @Operation(summary = "Completar: CONFIRMED -> COMPLETED (RN-09)")
    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    public ReservaRespuesta completar(@PathVariable Long id) {
        return servicio.completar(id);
    }
}
