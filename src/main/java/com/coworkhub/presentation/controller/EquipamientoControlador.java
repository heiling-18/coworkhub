package com.coworkhub.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.coworkhub.business.service.EquipamientoServicio;
import com.coworkhub.presentation.dto.request.EquipamientoSolicitud;
import com.coworkhub.presentation.dto.response.EquipamientoRespuesta;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Equipamientos", description = "Catálogo de equipamientos. Crear/modificar/eliminar: solo ADMIN.")
@RequestMapping("/api/equipamientos")
@RequiredArgsConstructor
public class EquipamientoControlador {

    private final EquipamientoServicio servicio;

    @GetMapping
    @Operation(summary = "Listar equipamientos")
    public List<EquipamientoRespuesta> listar() {
        return servicio.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar por id")
    public EquipamientoRespuesta obtener(@PathVariable Long id) {
        return servicio.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear (solo ADMIN)")
    public EquipamientoRespuesta crear(@Valid @RequestBody EquipamientoSolicitud solicitud) {
        return servicio.crear(solicitud);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar (solo ADMIN)")
    public EquipamientoRespuesta actualizar(@PathVariable Long id, @Valid @RequestBody EquipamientoSolicitud solicitud) {
        return servicio.actualizar(id, solicitud);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar (solo ADMIN). 409 si está en uso (RN-12)")
    public void eliminar(@PathVariable Long id) {
        servicio.eliminar(id);
    }
}
