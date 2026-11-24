package com.coworkhub.persistence.repository;

import com.coworkhub.persistence.entity.BookingItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingItemRepository extends JpaRepository<BookingItem, Long> {

    // RN-12: no se borra un servicio que esta en reservas
    boolean existsByServiceId(Long serviceId);
}
