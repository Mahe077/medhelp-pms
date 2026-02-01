package com.medhelp.pms.modules.auth_module.domain.repositories;

import com.medhelp.pms.modules.auth_module.domain.entities.RolePermission;
import com.medhelp.pms.modules.auth_module.domain.entities.RolePermissionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, RolePermissionId> {
    List<RolePermission> findAllByRoleId(UUID roleId);
}
