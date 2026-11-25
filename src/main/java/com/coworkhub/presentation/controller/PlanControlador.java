package com.coworkhub.presentation.controller;

import com.coworkhub.business.service.PlanServicio;
import com.coworkhub.presentation.dto.request.PlanSolicitud;
import com.coworkhub.presentation.dto.response.PlanRespuesta;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/planes")
@RequiredArgsConstructor
public class PlanControlador {

    private final PlanServicio servicio;

    @GetMapping
    public List<PlanRespuesta> listar() {
        return servicio.listar();
    }

    @GetMapping("/{id}")
    public PlanRespuesta obtener(@PathVariable Long id) {
        return servicio.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PlanRespuesta crear(@Valid @RequestBody PlanSolicitud solicitud) {
        return servicio.crear(solicitud);
    }

    @PutMapping("/{id}")
    public PlanRespuesta actualizar(@PathVariable Long id, @Valid @RequestBody PlanSolicitud solicitud) {
        return servicio.actualizar(id, solicitud);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        servicio.eliminar(id);
    }
}
