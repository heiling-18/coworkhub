package com.coworkhub.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.coworkhub.business.service.PlanServicio;
import com.coworkhub.presentation.dto.request.PlanSolicitud;
import com.coworkhub.presentation.dto.response.PlanRespuesta;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Planes", description = "Catálogo de planes de membresía. Crear/modificar/eliminar: solo ADMIN.")
@RequestMapping("/api/planes")
@RequiredArgsConstructor
public class PlanControlador {

    private final PlanServicio servicio;

    @GetMapping
    @Operation(summary = "Listar planes")
    public List<PlanRespuesta> listar() {
        return servicio.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar por id")
    public PlanRespuesta obtener(@PathVariable Long id) {
        return servicio.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear (solo ADMIN)")
    public PlanRespuesta crear(@Valid @RequestBody PlanSolicitud solicitud) {
        return servicio.crear(solicitud);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar (solo ADMIN)")
    public PlanRespuesta actualizar(@PathVariable Long id, @Valid @RequestBody PlanSolicitud solicitud) {
        return servicio.actualizar(id, solicitud);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar (solo ADMIN). 409 si está en uso (RN-12)")
    public void eliminar(@PathVariable Long id) {
        servicio.eliminar(id);
    }
}
