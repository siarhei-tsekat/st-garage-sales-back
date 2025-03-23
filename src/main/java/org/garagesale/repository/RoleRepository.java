package org.garagesale.repository;

import org.garagesale.security.RoleName;
import org.garagesale.security.AuthRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<AuthRole, Long> {

    Optional<AuthRole> findByRoleName(RoleName roleName);
}
