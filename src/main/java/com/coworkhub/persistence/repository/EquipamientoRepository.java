package com.coworkhub.persistence.repository;

import com.coworkhub.persistence.entity.Equipamiento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EquipamientoRepository extends JpaRepository<Equipamiento, Long> {

    List<Equipamiento> findAllByOrderByIdAsc();

    // RN-11: nombre unico (al crear y al actualizar)
    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);
}
