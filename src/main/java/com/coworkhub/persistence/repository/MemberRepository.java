package com.coworkhub.persistence.repository;

import com.coworkhub.persistence.entity.Member;
import com.coworkhub.persistence.enums.MemberStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    /** RF-02: filtros opcionales por nombre y estado. "join fetch" trae el plan en la misma consulta (sin N+1). */
    @Query("""
            select m from Member m join fetch m.plan
            where (:name is null or lower(m.fullName) like lower(concat('%', :name, '%')))
              and (:status is null or m.status = :status)
            order by m.id""")
    List<Member> search(@Param("name") String name, @Param("status") MemberStatus status);

    @EntityGraph(attributePaths = "plan")
    Optional<Member> findWithPlanById(Long id);

    // RN-11: documento y email unicos
    boolean existsByDocumentNumber(String documentNumber);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

    // RN-12: no se borra un plan con miembros
    boolean existsByPlanId(Long planId);
}
