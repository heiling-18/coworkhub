package com.coworkhub.presentation.dto.response;

/** Formato único de error de toda la API: {"error", "codigo", "timestamp"}. */
public record ErrorRespuesta(String error, String codigo, String timestamp) {
}
