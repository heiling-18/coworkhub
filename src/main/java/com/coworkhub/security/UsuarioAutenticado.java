package com.coworkhub.security;

import com.coworkhub.persistence.enums.Role;

/** Datos del usuario que viajan dentro del JWT (stateless: no se consulta la BD en cada petición). */
public record UsuarioAutenticado(String usuario, Role rol, Long miembroId) {

    public boolean esMiembro() {
        return rol == Role.MEMBER;
    }
}
