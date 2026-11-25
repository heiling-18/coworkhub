package com.coworkhub.presentation.dto.response;

import com.coworkhub.persistence.enums.Role;

public record LoginRespuesta(String token, String tipo, long expiraEnMinutos, String usuario, Role rol,
                             Long miembroId) {
}
