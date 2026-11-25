package com.coworkhub.business.exception;

/** 400: datos de entrada inválidos que no cubre Bean Validation. */
public class DatosInvalidosException extends RuntimeException {
    public DatosInvalidosException(String mensaje) {
        super(mensaje);
    }
}
