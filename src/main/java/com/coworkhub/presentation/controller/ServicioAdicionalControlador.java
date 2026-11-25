package com.coworkhub.presentation.controller;

import org.springframework.security.access.prepost.PreAuthorize;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.coworkhub.business.service.ServicioAdicionalServicio;
import com.coworkhub.presentation.dto.request.ServicioAdicionalSolicitud;
import com.coworkhub.presentation.dto.response.ServicioAdicionalRespuesta;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Servicios adicionales", description = "Catálogo de servicios que se agregan a una reserva. Crear/modificar/eliminar: solo ADMIN.")
@RequestMapping("/api/servicios-adicionales")
@RequiredArgsConstructor
public class ServicioAdicionalControlador {

    private final ServicioAdicionalServicio servicio;

    @GetMapping
    @Operation(summary = "Listar servicios adicionales")
    public List<ServicioAdicionalRespuesta> listar() {
        return servicio.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar por id")
    public ServicioAdicionalRespuesta obtener(@PathVariable Long id) {
        return servicio.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear (solo ADMIN)")
    @PreAuthorize("hasRole('ADMIN')")
    public ServicioAdicionalRespuesta crear(@Valid @RequestBody ServicioAdicionalSolicitud solicitud) {
        return servicio.crear(solicitud);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar (solo ADMIN)")
    @PreAuthorize("hasRole('ADMIN')")
    public ServicioAdicionalRespuesta actualizar(@PathVariable Long id, @Valid @RequestBody ServicioAdicionalSolicitud solicitud) {
        return servicio.actualizar(id, solicitud);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar (solo ADMIN). 409 si está en uso (RN-12)")
    @PreAuthorize("hasRole('ADMIN')")
    public void eliminar(@PathVariable Long id) {
        servicio.eliminar(id);
    }
}
