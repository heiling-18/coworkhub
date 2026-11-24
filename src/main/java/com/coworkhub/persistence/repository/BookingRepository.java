package com.coworkhub.persistence.repository;

import com.coworkhub.persistence.entity.Booking;
import com.coworkhub.persistence.enums.BookingStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    /**
     * RN-01: hay cruce si (inicioExistente < finNuevo) y (finExistente > inicioNuevo).
     * Terminar justo cuando empieza otra NO es cruce.
     */
    boolean existsByRoomIdAndStatusInAndStartTimeLessThanAndEndTimeGreaterThan(
            Long roomId, Collection<BookingStatus> statuses, LocalDateTime end, LocalDateTime start);

    /** RN-06: reservas activas futuras del miembro. */
    long countByMemberIdAndStatusInAndStartTimeAfter(Long memberId, Collection<BookingStatus> statuses,
                                                     LocalDateTime now);

    // RN-12
    boolean existsByRoomId(Long roomId);

    boolean existsByMemberId(Long memberId);

    /** Reservas que consumen horas del mes: activas/completadas y canceladas con cargo (no devuelven horas). */
    @Query("""
            select b from Booking b
            where b.member.id = :memberId
              and b.startTime >= :from and b.startTime < :to
              and (b.status in :statuses or (b.status = :cancelled and b.cancellationFee > 0))""")
    List<Booking> monthlyUsage(@Param("memberId") Long memberId, @Param("from") LocalDateTime from,
                               @Param("to") LocalDateTime to, @Param("statuses") Collection<BookingStatus> statuses,
                               @Param("cancelled") BookingStatus cancelled);

    /** RF-03: ids de las salas de una sede ocupadas en el rango pedido. */
    @Query("""
            select distinct b.room.id from Booking b
            where b.room.location.id = :locationId and b.status in :statuses
              and b.startTime < :end and b.endTime > :start""")
    List<Long> busyRoomIds(@Param("locationId") Long locationId, @Param("statuses") Collection<BookingStatus> statuses,
                           @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // Detalle y listados traen sala, miembro e items en una sola consulta (sin N+1)
    @EntityGraph(attributePaths = {"room", "member", "items", "items.service"})
    Optional<Booking> findDetailById(Long id);

    @EntityGraph(attributePaths = {"room", "member", "items", "items.service"})
    List<Booking> findAllByOrderByStartTimeAsc();

    @EntityGraph(attributePaths = {"room", "member", "items", "items.service"})
    List<Booking> findByMemberIdOrderByStartTimeAsc(Long memberId);

    @EntityGraph(attributePaths = {"room", "member", "items", "items.service"})
    List<Booking> findByMemberIdAndStatusOrderByStartTimeAsc(Long memberId, BookingStatus status);

    @EntityGraph(attributePaths = {"room", "member", "items", "items.service"})
    List<Booking> findByStatusOrderByStartTimeAsc(BookingStatus status);

    @EntityGraph(attributePaths = {"room", "member", "items", "items.service"})
    List<Booking> findByRoomIdAndStartTimeGreaterThanEqualAndStartTimeLessThanOrderByStartTimeAsc(
            Long roomId, LocalDateTime from, LocalDateTime to);
}
