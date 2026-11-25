package com.coworkhub.security;

import com.coworkhub.business.exception.AccesoDenegadoException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Objects;

/** Da acceso al usuario de la petición y valida la regla de "dueño" (RN-13). */
@Component
public class UsuarioActual {

    public UsuarioAutenticado obtener() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UsuarioAutenticado usuario)) {
            throw new AccesoDenegadoException("No hay un usuario autenticado");
        }
        return usuario;
    }

    /** Un MEMBER solo puede ver/modificar lo suyo. ADMIN y RECEPTIONIST pueden todo. */
    public void verificarDueno(Long miembroId) {
        UsuarioAutenticado usuario = obtener();
        if (usuario.esMiembro() && !Objects.equals(usuario.miembroId(), miembroId)) {
            throw new AccesoDenegadoException("Solo puede acceder a su propia información");
        }
    }
}
