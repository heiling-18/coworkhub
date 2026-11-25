package com.coworkhub.business.service.impl;

import com.coworkhub.business.exception.ReglaNegocioException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;

/** Cálculos puros de RN-07 (costo) y RN-08 (cancelación). No usa la BD: fácil de probar. */
@Component
public class CalculadoraCostos {

    private static final BigDecimal CIEN = BigDecimal.valueOf(100);
    private static final BigDecimal SESENTA = BigDecimal.valueOf(60);

    /**
     * RN-07: las horas incluidas que le quedan al miembro en el mes no se cobran.
     * Las excedentes cuestan hourlyRate x (1 - descuento%).
     */
    public BigDecimal costoSala(long minutosReserva, long minutosYaUsadosMes, int horasIncluidasMes,
                                BigDecimal tarifaHora, BigDecimal descuentoPorcentaje) {
        long incluidosRestantes = Math.max(0, horasIncluidasMes * 60L - minutosYaUsadosMes);
        long minutosCobrables = Math.max(0, minutosReserva - incluidosRestantes);
        BigDecimal horasCobrables = BigDecimal.valueOf(minutosCobrables).divide(SESENTA, 4, RoundingMode.HALF_UP);
        BigDecimal factor = BigDecimal.ONE.subtract(descuentoPorcentaje.divide(CIEN, 4, RoundingMode.HALF_UP));
        return tarifaHora.multiply(horasCobrables).multiply(factor).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * RN-08: 24 h o más antes -> sin cargo. Entre 24 h y 2 h -> 50 % del costo de la sala.
     * Menos de 2 h o ya iniciada -> no se puede cancelar.
     */
    public BigDecimal cargoCancelacion(LocalDateTime ahora, LocalDateTime inicio, BigDecimal costoSala) {
        long minutosAntes = Duration.between(ahora, inicio).toMinutes();
        if (minutosAntes >= 24 * 60) {
            return BigDecimal.ZERO.setScale(2);
        }
        if (minutosAntes >= 2 * 60) {
            return costoSala.multiply(new BigDecimal("0.50")).setScale(2, RoundingMode.HALF_UP);
        }
        throw new ReglaNegocioException("RN-08",
                "No se puede cancelar con menos de 2 horas de anticipación o si la reserva ya inició");
    }
}
