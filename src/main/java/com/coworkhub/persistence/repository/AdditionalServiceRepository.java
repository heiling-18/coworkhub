package com.coworkhub.persistence.repository;

import com.coworkhub.persistence.entity.AdditionalService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdditionalServiceRepository extends JpaRepository<AdditionalService, Long> {

    List<AdditionalService> findAllByOrderByIdAsc();

    // RN-11: nombre unico (al crear y al actualizar)
    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
}
