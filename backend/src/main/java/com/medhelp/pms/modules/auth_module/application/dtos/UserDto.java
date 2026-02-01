package com.medhelp.pms.modules.auth_module.application.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import com.medhelp.pms.modules.auth_module.application.dtos.RoleDto;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private UUID id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private Set<RoleDto> roles;
    private String licenseNumber;
    private String userType;
    private Boolean isActive;
    private Boolean isEmailVerified;
    private String preferredLanguage;
    private String preferredTheme;
    private LocalDateTime lastLoginAt;
    private Set<String> permissions;
    private LocalDateTime createdAt;
}