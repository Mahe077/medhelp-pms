package com.medhelp.pms.modules.auth_module.domain.services;

import com.medhelp.pms.modules.auth_module.domain.entities.Role;
import com.medhelp.pms.modules.auth_module.domain.repositories.RoleRepository;
import com.medhelp.pms.shared.domain.exceptions.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final AccessControlService accessControlService; // reusing for auditing for now, or move audit to event
                                                             // publisher directly

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Role getRoleById(UUID id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Role not found"));
    }

    @Transactional
    public Role createRole(String name, String description) {
        return accessControlService.createRole(name, description);
    }
}
