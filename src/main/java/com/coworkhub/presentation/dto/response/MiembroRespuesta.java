package com.coworkhub.presentation.dto.response;

import com.coworkhub.persistence.enums.MemberStatus;

public record MiembroRespuesta(Long id, String numeroDocumento, String nombreCompleto, String email,
                               MemberStatus estado, Long planId, String planNombre) {
}
