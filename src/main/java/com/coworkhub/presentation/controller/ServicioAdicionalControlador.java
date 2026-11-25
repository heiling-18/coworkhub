package com.coworkhub.presentation.controller;

import com.coworkhub.business.service.ServicioAdicionalServicio;
import com.coworkhub.presentation.dto.request.ServicioAdicionalSolicitud;
import com.coworkhub.presentation.dto.response.ServicioAdicionalRespuesta;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/servicios-adicionales")
@RequiredArgsConstructor
public class ServicioAdicionalControlador {

    private final ServicioAdicionalServicio servicio;

    @GetMapping
    public List<ServicioAdicionalRespuesta> listar() {
        return servicio.listar();
    }

    @GetMapping("/{id}")
    public ServicioAdicionalRespuesta obtener(@PathVariable Long id) {
        return servicio.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ServicioAdicionalRespuesta crear(@Valid @RequestBody ServicioAdicionalSolicitud solicitud) {
        return servicio.crear(solicitud);
    }

    @PutMapping("/{id}")
    public ServicioAdicionalRespuesta actualizar(@PathVariable Long id, @Valid @RequestBody ServicioAdicionalSolicitud solicitud) {
        return servicio.actualizar(id, solicitud);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        servicio.eliminar(id);
    }
}
