package com.coworkhub.persistence.repository;

import com.coworkhub.persistence.entity.Sala;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SalaRepository extends JpaRepository<Sala, Long> {

    // @EntityGraph trae sede y equipamientos en la misma consulta: evita N+1
    @EntityGraph(attributePaths = {"sede", "equipamientos"})
    List<Sala> findAllByOrderByIdAsc();

    @EntityGraph(attributePaths = {"sede", "equipamientos"})
    List<Sala> findBySedeIdOrderByIdAsc(Long sedeId);

    @EntityGraph(attributePaths = {"sede", "equipamientos"})
    List<Sala> findBySedeIdAndActivaTrueOrderByIdAsc(Long sedeId);

    @EntityGraph(attributePaths = {"sede", "equipamientos"})
    Optional<Sala> findDetalleById(Long id);

    boolean existsByNombreIgnoreCaseAndSedeId(String nombre, Long sedeId);

    boolean existsByNombreIgnoreCaseAndSedeIdAndIdNot(String nombre, Long sedeId, Long id);

    boolean existsBySedeId(Long sedeId);

    boolean existsByEquipamientosId(Long equipamientoId);

    /**
     * RN-01 con concurrencia: bloquea la fila de la sala (SELECT ... FOR UPDATE).
     * Dos reservas simultaneas de la misma sala se atienden una detras de otra.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Sala s join fetch s.sede where s.id = :id")
    Optional<Sala> bloquearPorId(@Param("id") Long id);
}
