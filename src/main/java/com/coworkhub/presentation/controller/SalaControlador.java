package com.coworkhub.presentation.controller;

import com.coworkhub.business.service.SalaServicio;
import com.coworkhub.presentation.dto.request.SalaSolicitud;
import com.coworkhub.presentation.dto.response.SalaRespuesta;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/salas")
@RequiredArgsConstructor
public class SalaControlador {

    private final SalaServicio servicio;

    @GetMapping
    public List<SalaRespuesta> listar(@RequestParam(required = false) Long sedeId) {
        return servicio.listar(sedeId);
    }

    @GetMapping("/{id}")
    public SalaRespuesta obtener(@PathVariable Long id) {
        return servicio.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SalaRespuesta crear(@Valid @RequestBody SalaSolicitud solicitud) {
        return servicio.crear(solicitud);
    }

    @PutMapping("/{id}")
    public SalaRespuesta actualizar(@PathVariable Long id, @Valid @RequestBody SalaSolicitud solicitud) {
        return servicio.actualizar(id, solicitud);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        servicio.eliminar(id);
    }
}
