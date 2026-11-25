package com.coworkhub.business.exception;

/** 403: autenticado, pero no es dueño del recurso (RN-13). */
public class AccesoDenegadoException extends RuntimeException {
    public AccesoDenegadoException(String mensaje) {
        super(mensaje);
    }
}
