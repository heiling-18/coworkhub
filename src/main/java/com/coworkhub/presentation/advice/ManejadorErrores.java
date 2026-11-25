package com.coworkhub.presentation.advice;

import com.coworkhub.business.exception.DatosInvalidosException;
import com.coworkhub.business.exception.NoEncontradoException;
import com.coworkhub.business.exception.ReglaNegocioException;
import com.coworkhub.presentation.dto.response.ErrorRespuesta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.Clock;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

/**
 * Módulo 07: convierte cada excepción en una respuesta HTTP con el mismo cuerpo
 * {error, codigo, timestamp}. Ninguna regla de negocio responde 500.
 */
@RestControllerAdvice
public class ManejadorErrores {

    private static final Logger log = LoggerFactory.getLogger(ManejadorErrores.class);
    private final Clock reloj;

    public ManejadorErrores(@Qualifier("relojNegocio") Clock reloj) {
        this.reloj = reloj;
    }

    /** 404: el recurso no existe. */
    @ExceptionHandler(NoEncontradoException.class)
    public ResponseEntity<ErrorRespuesta> noEncontrado(NoEncontradoException e) {
        return responder(HttpStatus.NOT_FOUND, e.getMessage(), "NO_ENCONTRADO");
    }

    /** 409: se viola una regla de negocio; el código es la regla (RN-01, RN-08...). */
    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<ErrorRespuesta> reglaNegocio(ReglaNegocioException e) {
        return responder(HttpStatus.CONFLICT, e.getMessage(), e.getCodigo());
    }

    /** 400: datos inválidos detectados en el negocio. */
    @ExceptionHandler(DatosInvalidosException.class)
    public ResponseEntity<ErrorRespuesta> datosInvalidos(DatosInvalidosException e) {
        return responder(HttpStatus.BAD_REQUEST, e.getMessage(), "VALIDACION");
    }

    /** 400: falló @Valid en el cuerpo (ej. sede sin nombre). */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorRespuesta> validacion(MethodArgumentNotValidException e) {
        String detalle = e.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return responder(HttpStatus.BAD_REQUEST, detalle, "VALIDACION");
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorRespuesta> validacionParametros(HandlerMethodValidationException e) {
        return responder(HttpStatus.BAD_REQUEST, "Parámetros inválidos", "VALIDACION");
    }

    /** 400: JSON mal escrito, tipo equivocado, parámetro faltante o fecha con formato inválido. */
    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class, DateTimeParseException.class})
    public ResponseEntity<ErrorRespuesta> malFormada(Exception e) {
        String mensaje = e instanceof MissingServletRequestParameterException m
                ? "Falta el parámetro obligatorio '" + m.getParameterName() + "'"
                : "La solicitud está mal formada (revise el JSON, los tipos y el formato de las fechas)";
        return responder(HttpStatus.BAD_REQUEST, mensaje, "VALIDACION");
    }

    /** 409: respaldo de RN-11 si la base de datos rechaza un duplicado. */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorRespuesta> integridad(DataIntegrityViolationException e) {
        return responder(HttpStatus.CONFLICT, "El registro está duplicado o en uso", "RN-11");
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorRespuesta> rutaInexistente(NoResourceFoundException e) {
        return responder(HttpStatus.NOT_FOUND, "La ruta no existe", "NO_ENCONTRADO");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorRespuesta> metodo(HttpRequestMethodNotSupportedException e) {
        return responder(HttpStatus.METHOD_NOT_ALLOWED, "Método HTTP no permitido", "METODO_NO_PERMITIDO");
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorRespuesta> tipoContenido(HttpMediaTypeNotSupportedException e) {
        return responder(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Envíe el cuerpo como application/json", "TIPO_NO_SOPORTADO");
    }

    /** 500: cualquier error inesperado. El detalle va solo al log, nunca al cliente. */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorRespuesta> inesperado(Exception e) {
        log.error("Error inesperado", e);
        return responder(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor", "ERROR_INTERNO");
    }

    private ResponseEntity<ErrorRespuesta> responder(HttpStatus estado, String mensaje, String codigo) {
        return ResponseEntity.status(estado).body(new ErrorRespuesta(mensaje, codigo,
                Instant.now(reloj).truncatedTo(ChronoUnit.SECONDS).toString()));
    }
}
