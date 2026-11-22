package com.coworkhub.persistence.repository;

import com.coworkhub.persistence.entity.CuentaUsuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CuentaUsuarioRepository extends JpaRepository<CuentaUsuario, Long> {

    Optional<CuentaUsuario> findByNombreUsuario(String nombreUsuario);

    Optional<CuentaUsuario> findByMiembroId(Long miembroId);

    boolean existsByNombreUsuario(String nombreUsuario);
}
