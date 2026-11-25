package com.coworkhub.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

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
@Tag(name = "Miembros", description = "Gestión de miembros: ADMIN y RECEPTIONIST. Un MEMBER solo consulta lo suyo.")
@RequestMapping("/api/miembros")
@RequiredArgsConstructor
public class MiembroControlador {

    private final MiembroServicio servicio;

    /** GET /api/miembros?nombre=&estado= */
    @GetMapping
    @Operation(summary = "Listar miembros (filtros ?nombre= y ?estado=)")
    public List<MiembroRespuesta> listar(@RequestParam(required = false) String nombre,
                                         @RequestParam(required = false) MemberStatus estado) {
        return servicio.listar(nombre, estado);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar miembro (MEMBER solo el suyo)")
    public MiembroRespuesta obtener(@PathVariable Long id) {
        return servicio.obtener(id);
    }

    /** Registra el miembro y su cuenta de usuario. */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registrar miembro y su cuenta de usuario")
    public MiembroRespuesta registrar(@Valid @RequestBody MiembroRegistroSolicitud solicitud) {
        return servicio.registrar(solicitud);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar nombre, email y plan")
    public MiembroRespuesta actualizar(@PathVariable Long id, @Valid @RequestBody MiembroActualizacionSolicitud solicitud) {
        return servicio.actualizar(id, solicitud);
    }

    @PatchMapping("/{id}/suspender")
    @Operation(summary = "Suspender miembro (ya no podrá reservar, RN-05)")
    public MiembroRespuesta suspender(@PathVariable Long id) {
        return servicio.cambiarEstado(id, MemberStatus.SUSPENDED);
    }

    @PatchMapping("/{id}/activar")
    @Operation(summary = "Reactivar miembro")
    public MiembroRespuesta activar(@PathVariable Long id) {
        return servicio.cambiarEstado(id, MemberStatus.ACTIVE);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar miembro (409 si tiene reservas, RN-12)")
    public void eliminar(@PathVariable Long id) {
        servicio.eliminar(id);
    }

    /** RF-10: GET /api/miembros/{id}/consumo?mes=2026-11 (por defecto el mes actual). */
    @GetMapping("/{id}/consumo")
    @Operation(summary = "Consumo mensual: horas incluidas, usadas, restantes y excedentes")
    public ConsumoRespuesta consumo(@PathVariable Long id, @RequestParam(required = false) String mes) {
        return servicio.consumo(id, mes == null ? YearMonth.now() : YearMonth.parse(mes));
    }
}
