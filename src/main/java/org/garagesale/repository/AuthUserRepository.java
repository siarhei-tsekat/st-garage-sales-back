package org.garagesale.repository;

import org.garagesale.security.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthUserRepository extends JpaRepository<AuthUser, Long> {

    Optional<AuthUser> findByUserName(String username);

    boolean existsByUserName(String username);

    boolean existsByEmail(String email);
}
