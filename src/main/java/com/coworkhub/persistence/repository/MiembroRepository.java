package com.coworkhub.persistence.repository;

import com.coworkhub.persistence.entity.Miembro;
import com.coworkhub.persistence.enums.EstadoMiembro;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MiembroRepository extends JpaRepository<Miembro, Long> {

    /** RF-02: filtros opcionales por nombre y estado. "join fetch" trae el plan en la misma consulta (sin N+1). */
    @Query("""
            select m from Miembro m join fetch m.plan
            where (:nombre is null or lower(m.nombreCompleto) like lower(concat('%', :nombre, '%')))
              and (:estado is null or m.estado = :estado)
            order by m.id""")
    List<Miembro> buscar(@Param("nombre") String nombre, @Param("estado") EstadoMiembro estado);

    @EntityGraph(attributePaths = "plan")
    Optional<Miembro> findConPlanById(Long id);

    // RN-11: documento y correo unicos
    boolean existsByNumeroDocumento(String numeroDocumento);

    boolean existsByCorreoElectronicoIgnoreCase(String correo);

    boolean existsByCorreoElectronicoIgnoreCaseAndIdNot(String correo, Long id);

    // RN-12: no se borra un plan con miembros
    boolean existsByPlanId(Long planId);
}
