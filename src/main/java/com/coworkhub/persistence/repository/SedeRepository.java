package com.coworkhub.persistence.repository;

import com.coworkhub.persistence.entity.Sede;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SedeRepository extends JpaRepository<Sede, Long> {

    List<Sede> findAllByOrderByIdAsc();
}
