package com.coworkhub.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.coworkhub.business.service.SedeServicio;
import com.coworkhub.presentation.dto.request.SedeSolicitud;
import com.coworkhub.presentation.dto.response.SedeRespuesta;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@Tag(name = "Sedes", description = "Catálogo de sedes. Consultar: autenticado. Crear/modificar/eliminar: solo ADMIN.") 
@RequestMapping ("/api/sedes")
public class SedeControlador {
private final SedeServicio sedeServicio;

    public SedeControlador(SedeServicio sedeServicio) {
        this.sedeServicio = sedeServicio;
    }

    @GetMapping
    @Operation(summary = "Listar sedes")
    public List<SedeRespuesta> listar() {
        return sedeServicio.listar();
    }
    @GetMapping ("/{id}")
    @Operation(summary = "Consultar por id")
    public SedeRespuesta obtener(@PathVariable Long id) {
        return sedeServicio.obtener(id);
    }
    @PostMapping 
    @ResponseStatus (HttpStatus.CREATED)
    @Operation(summary = "Crear (solo ADMIN)")
    public SedeRespuesta crear(@Valid @RequestBody SedeSolicitud solicitud) {
        return sedeServicio.crear(solicitud);
    }
    @PutMapping ("/{id}")
    @Operation(summary = "Actualizar (solo ADMIN)")
    public SedeRespuesta actualizar(@PathVariable Long id, @Valid @RequestBody SedeSolicitud solicitud) {
        return sedeServicio.actualizar(id, solicitud);
    }
     @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar (solo ADMIN). 409 si está en uso (RN-12)")
    public void eliminar(@PathVariable Long id) {  
        sedeServicio.eliminar(id);
    }
}
