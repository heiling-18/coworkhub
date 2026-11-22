package com.coworkhub.persistence.repository;

import com.coworkhub.persistence.entity.ItemReserva;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemReservaRepository extends JpaRepository<ItemReserva, Long> {

    boolean existsByServicioId(Long servicioId);
}
