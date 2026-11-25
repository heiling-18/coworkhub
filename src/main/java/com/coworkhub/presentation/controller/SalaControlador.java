package com.coworkhub.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.coworkhub.business.service.SalaServicio;
import com.coworkhub.presentation.dto.request.SalaSolicitud;
import com.coworkhub.presentation.dto.response.SalaRespuesta;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Salas", description = "Catálogo de salas con su equipamiento. Crear/modificar/eliminar: solo ADMIN.")
@RequestMapping("/api/salas")
@RequiredArgsConstructor
public class SalaControlador {

    private final SalaServicio servicio;

    @GetMapping
    @Operation(summary = "Listar salas (filtro opcional ?sedeId=)")
    public List<SalaRespuesta> listar(@RequestParam(required = false) Long sedeId) {
        return servicio.listar(sedeId);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar por id")
    public SalaRespuesta obtener(@PathVariable Long id) {
        return servicio.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear (solo ADMIN)")
    public SalaRespuesta crear(@Valid @RequestBody SalaSolicitud solicitud) {
        return servicio.crear(solicitud);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar (solo ADMIN)")
    public SalaRespuesta actualizar(@PathVariable Long id, @Valid @RequestBody SalaSolicitud solicitud) {
        return servicio.actualizar(id, solicitud);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar (solo ADMIN). 409 si está en uso (RN-12)")
    public void eliminar(@PathVariable Long id) {
        servicio.eliminar(id);
    }
}
