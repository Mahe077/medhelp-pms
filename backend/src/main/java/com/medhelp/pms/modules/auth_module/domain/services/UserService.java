package com.medhelp.pms.modules.auth_module.domain.services;

import com.medhelp.pms.modules.auth_module.domain.entities.Role;
import com.medhelp.pms.modules.auth_module.domain.entities.User;
import com.medhelp.pms.modules.auth_module.domain.repositories.AuthRepository;
import com.medhelp.pms.modules.auth_module.domain.repositories.RoleRepository;
import com.medhelp.pms.shared.domain.exceptions.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final AuthRepository authRepository;
    private final RoleRepository roleRepository;

    public List<User> getAllUsers() {
        return authRepository.findAll();
    }

    @Transactional
    public void assignRoleToUser(UUID userId, UUID roleId) {
        User user = authRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found"));
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new BusinessException("Role not found"));

        user.getRoles().add(role);
        authRepository.save(user);
    }

    @Transactional
    public void removeRoleFromUser(UUID userId, UUID roleId) {
        User user = authRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found"));
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new BusinessException("Role not found"));

        user.getRoles().remove(role);
        authRepository.save(user);
    }
}
