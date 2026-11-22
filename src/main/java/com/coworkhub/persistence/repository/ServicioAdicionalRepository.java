package com.coworkhub.persistence.repository;

import com.coworkhub.persistence.entity.ServicioAdicional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServicioAdicionalRepository extends JpaRepository<ServicioAdicional, Long> {

    List<ServicioAdicional> findAllByOrderByIdAsc();

    // RN-11: nombre unico (al crear y al actualizar)
    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);
}
