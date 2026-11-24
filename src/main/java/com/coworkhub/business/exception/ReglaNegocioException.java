package com.coworkhub.business.exception;

/** 409: se viola una regla de negocio. El codigo es la regla (RN-01, RN-12...). */
public class ReglaNegocioException extends RuntimeException {

    private final String codigo;

    public ReglaNegocioException(String codigo, String mensaje) {
        super(mensaje);
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }
}
