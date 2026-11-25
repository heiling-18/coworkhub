package com.coworkhub.presentation.dto.response;

import com.coworkhub.persistence.enums.Role;

/** RF-14: datos de quien inició sesión. */
public record PerfilRespuesta(String usuario, Role rol, Long miembroId) {
}
