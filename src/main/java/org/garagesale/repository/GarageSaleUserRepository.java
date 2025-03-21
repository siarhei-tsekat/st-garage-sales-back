package org.garagesale.repository;

import org.garagesale.security.GarageSaleUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GarageSaleUserRepository extends JpaRepository<GarageSaleUser, Long> {

    Optional<GarageSaleUser> findByUserName(String username);

    boolean existsByUserName(String username);

    boolean existsByEmail(String email);
}
