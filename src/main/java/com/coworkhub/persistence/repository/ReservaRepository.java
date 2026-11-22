package com.coworkhub.persistence.repository;

import com.coworkhub.persistence.entity.Reserva;
import com.coworkhub.persistence.enums.EstadoReserva;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    /**
     * RN-01: hay cruce si (inicioExistente < finNuevo) y (finExistente > inicioNuevo).
     * Terminar justo cuando empieza otra NO es cruce.
     */
    boolean existsBySalaIdAndEstadoInAndFechaHoraInicioLessThanAndFechaHoraFinGreaterThan(
            Long salaId, Collection<EstadoReserva> estados, LocalDateTime fin, LocalDateTime inicio);

    /** RN-06: reservas activas futuras del miembro. */
    long countByMiembroIdAndEstadoInAndFechaHoraInicioAfter(Long miembroId, Collection<EstadoReserva> estados,
                                                           LocalDateTime ahora);

    // RN-12
    boolean existsBySalaId(Long salaId);

    boolean existsByMiembroId(Long miembroId);

    /** Reservas que consumen horas del mes: activas/completadas y canceladas con cargo (esas no devuelven horas). */
    @Query("""
            select r from Reserva r
            where r.miembro.id = :miembroId
              and r.fechaHoraInicio >= :desde and r.fechaHoraInicio < :hasta
              and (r.estado in :estados or (r.estado = :cancelada and r.cargoCancelacion > 0))""")
    List<Reserva> consumoDelMes(@Param("miembroId") Long miembroId, @Param("desde") LocalDateTime desde,
                                @Param("hasta") LocalDateTime hasta,
                                @Param("estados") Collection<EstadoReserva> estados,
                                @Param("cancelada") EstadoReserva cancelada);

    /** RF-03: ids de las salas de una sede ocupadas en el rango pedido. */
    @Query("""
            select distinct r.sala.id from Reserva r
            where r.sala.sede.id = :sedeId and r.estado in :estados
              and r.fechaHoraInicio < :fin and r.fechaHoraFin > :inicio""")
    List<Long> salasOcupadas(@Param("sedeId") Long sedeId, @Param("estados") Collection<EstadoReserva> estados,
                             @Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    // Todas las consultas de detalle/listado traen sala, miembro e items en una sola consulta (sin N+1)
    @EntityGraph(attributePaths = {"sala", "miembro", "items", "items.servicio"})
    Optional<Reserva> findDetalleById(Long id);

    @EntityGraph(attributePaths = {"sala", "miembro", "items", "items.servicio"})
    List<Reserva> findAllByOrderByFechaHoraInicioAsc();

    @EntityGraph(attributePaths = {"sala", "miembro", "items", "items.servicio"})
    List<Reserva> findByMiembroIdOrderByFechaHoraInicioAsc(Long miembroId);

    @EntityGraph(attributePaths = {"sala", "miembro", "items", "items.servicio"})
    List<Reserva> findByMiembroIdAndEstadoOrderByFechaHoraInicioAsc(Long miembroId, EstadoReserva estado);

    @EntityGraph(attributePaths = {"sala", "miembro", "items", "items.servicio"})
    List<Reserva> findByEstadoOrderByFechaHoraInicioAsc(EstadoReserva estado);

    @EntityGraph(attributePaths = {"sala", "miembro", "items", "items.servicio"})
    List<Reserva> findBySalaIdAndFechaHoraInicioGreaterThanEqualAndFechaHoraInicioLessThanOrderByFechaHoraInicioAsc(
            Long salaId, LocalDateTime desde, LocalDateTime hasta);
}
