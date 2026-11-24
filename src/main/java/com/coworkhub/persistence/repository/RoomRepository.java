package com.coworkhub.persistence.repository;

import com.coworkhub.persistence.entity.Room;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {

    // @EntityGraph trae sede y equipamiento en la misma consulta: evita N+1
    @EntityGraph(attributePaths = {"location", "equipment"})
    List<Room> findAllByOrderByIdAsc();

    @EntityGraph(attributePaths = {"location", "equipment"})
    List<Room> findByLocationIdOrderByIdAsc(Long locationId);

    @EntityGraph(attributePaths = {"location", "equipment"})
    List<Room> findByLocationIdAndActiveTrueOrderByIdAsc(Long locationId);

    @EntityGraph(attributePaths = {"location", "equipment"})
    Optional<Room> findDetailById(Long id);

    // RN-11: nombre unico dentro de la sede
    boolean existsByNameIgnoreCaseAndLocationId(String name, Long locationId);

    boolean existsByNameIgnoreCaseAndLocationIdAndIdNot(String name, Long locationId, Long id);

    // RN-12
    boolean existsByLocationId(Long locationId);

    boolean existsByEquipmentId(Long equipmentId);

    /**
     * RN-01 con concurrencia: bloquea la fila de la sala (SELECT ... FOR UPDATE).
     * Dos reservas simultaneas de la misma sala se atienden una detras de otra.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from Room r join fetch r.location where r.id = :id")
    Optional<Room> lockById(@Param("id") Long id);
}
