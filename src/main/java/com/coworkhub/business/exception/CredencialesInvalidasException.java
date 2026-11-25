package com.coworkhub.business.exception;

/** 401: usuario o contraseña incorrectos. */
public class CredencialesInvalidasException extends RuntimeException {
    public CredencialesInvalidasException(String mensaje) {
        super(mensaje);
    }
}
