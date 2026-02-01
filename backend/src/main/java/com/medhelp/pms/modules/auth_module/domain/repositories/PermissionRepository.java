package com.medhelp.pms.modules.auth_module.domain.repositories;

import com.medhelp.pms.modules.auth_module.domain.entities.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, UUID> {
    Optional<Permission> findByName(String name);

    boolean existsByName(String name);
}
