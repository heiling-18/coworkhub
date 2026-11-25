package com.coworkhub.presentation.controller;

import com.coworkhub.business.service.EquipamientoServicio;
import com.coworkhub.presentation.dto.request.EquipamientoSolicitud;
import com.coworkhub.presentation.dto.response.EquipamientoRespuesta;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/equipamientos")
@RequiredArgsConstructor
public class EquipamientoControlador {

    private final EquipamientoServicio servicio;

    @GetMapping
    public List<EquipamientoRespuesta> listar() {
        return servicio.listar();
    }

    @GetMapping("/{id}")
    public EquipamientoRespuesta obtener(@PathVariable Long id) {
        return servicio.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EquipamientoRespuesta crear(@Valid @RequestBody EquipamientoSolicitud solicitud) {
        return servicio.crear(solicitud);
    }

    @PutMapping("/{id}")
    public EquipamientoRespuesta actualizar(@PathVariable Long id, @Valid @RequestBody EquipamientoSolicitud solicitud) {
        return servicio.actualizar(id, solicitud);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        servicio.eliminar(id);
    }
}
