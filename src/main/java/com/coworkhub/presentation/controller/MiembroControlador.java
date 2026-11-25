package com.coworkhub.presentation.controller;

import com.coworkhub.business.service.MiembroServicio;
import com.coworkhub.persistence.enums.MemberStatus;
import com.coworkhub.presentation.dto.request.MiembroActualizacionSolicitud;
import com.coworkhub.presentation.dto.request.MiembroRegistroSolicitud;
import com.coworkhub.presentation.dto.response.ConsumoRespuesta;
import com.coworkhub.presentation.dto.response.MiembroRespuesta;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/miembros")
@RequiredArgsConstructor
public class MiembroControlador {

    private final MiembroServicio servicio;

    /** GET /api/miembros?nombre=&estado= */
    @GetMapping
    public List<MiembroRespuesta> listar(@RequestParam(required = false) String nombre,
                                         @RequestParam(required = false) MemberStatus estado) {
        return servicio.listar(nombre, estado);
    }

    @GetMapping("/{id}")
    public MiembroRespuesta obtener(@PathVariable Long id) {
        return servicio.obtener(id);
    }

    /** Registra el miembro y su cuenta de usuario. */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MiembroRespuesta registrar(@Valid @RequestBody MiembroRegistroSolicitud solicitud) {
        return servicio.registrar(solicitud);
    }

    @PutMapping("/{id}")
    public MiembroRespuesta actualizar(@PathVariable Long id, @Valid @RequestBody MiembroActualizacionSolicitud solicitud) {
        return servicio.actualizar(id, solicitud);
    }

    @PatchMapping("/{id}/suspender")
    public MiembroRespuesta suspender(@PathVariable Long id) {
        return servicio.cambiarEstado(id, MemberStatus.SUSPENDED);
    }

    @PatchMapping("/{id}/activar")
    public MiembroRespuesta activar(@PathVariable Long id) {
        return servicio.cambiarEstado(id, MemberStatus.ACTIVE);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        servicio.eliminar(id);
    }

    /** RF-10: GET /api/miembros/{id}/consumo?mes=2026-11 (por defecto el mes actual). */
    @GetMapping("/{id}/consumo")
    public ConsumoRespuesta consumo(@PathVariable Long id, @RequestParam(required = false) String mes) {
        return servicio.consumo(id, mes == null ? YearMonth.now() : YearMonth.parse(mes));
    }
}
