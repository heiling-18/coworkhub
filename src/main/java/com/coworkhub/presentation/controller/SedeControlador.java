package com.coworkhub.presentation.controller;

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
@RequestMapping ("/api/sedes")
public class SedeControlador {
private final SedeServicio sedeServicio;

    public SedeControlador(SedeServicio sedeServicio) {
        this.sedeServicio = sedeServicio;
    }

    @GetMapping
    public List<SedeRespuesta> listar() {
        return sedeServicio.listar();
    }
    @GetMapping ("/{id}")
    public SedeRespuesta obtener(@PathVariable Long id) {
        return sedeServicio.obtener(id);
    }
    @PostMapping 
    @ResponseStatus (HttpStatus.CREATED)
    public SedeRespuesta crear(@Valid @RequestBody SedeSolicitud solicitud) {
        return sedeServicio.crear(solicitud);
    }
    @PutMapping ("/{id}")
    public SedeRespuesta actualizar(@PathVariable Long id, @Valid @RequestBody SedeSolicitud solicitud) {
        return sedeServicio.actualizar(id, solicitud);
    }
     @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {  
        sedeServicio.eliminar(id);
    }
}
