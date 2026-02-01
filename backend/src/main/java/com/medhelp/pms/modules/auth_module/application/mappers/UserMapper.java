package com.medhelp.pms.modules.auth_module.application.mappers;

import com.medhelp.pms.modules.auth_module.application.dtos.UserDto;
import com.medhelp.pms.modules.auth_module.domain.entities.User;
import com.medhelp.pms.modules.auth_module.domain.entities.Role;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserDto toDto(User user) {
        if (user == null) {
            return null;
        }

        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .roles(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()))
                .licenseNumber(user.getLicenseNumber())
                .userType(user.getUserType() != null ? user.getUserType().name() : null)
                .isActive(user.getIsActive())
                .isEmailVerified(user.getIsEmailVerified())
                .preferredLanguage(user.getPreferredLanguage())
                .preferredTheme(user.getPreferredTheme())
                .lastLoginAt(user.getLastLoginAt())
                .permissions(user.getPermissions())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
