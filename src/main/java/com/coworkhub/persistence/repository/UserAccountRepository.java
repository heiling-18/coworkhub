package com.coworkhub.persistence.repository;

import com.coworkhub.persistence.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

    Optional<UserAccount> findByUsername(String username);

    Optional<UserAccount> findByMemberId(Long memberId);

    boolean existsByUsername(String username);
}
