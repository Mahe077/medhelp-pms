package com.medhelp.pms.modules.auth_module.domain.services;

import com.medhelp.pms.modules.auth_module.domain.entities.Permission;
import com.medhelp.pms.modules.auth_module.domain.repositories.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final PermissionRepository permissionRepository;
    private final AccessControlService accessControlService; // reusing for consistency

    public List<Permission> getAllPermissions() {
        return permissionRepository.findAll();
    }

    @Transactional
    public Permission createPermission(String name, String description, String resource, String action) {
        return accessControlService.createPermission(name, description, resource, action);
    }
}
