package com.medhelp.pms.modules.auth_module.application.mappers;

import com.medhelp.pms.modules.auth_module.application.dtos.RoleDto;
import com.medhelp.pms.modules.auth_module.domain.entities.Role;
import org.springframework.stereotype.Component;

@Component
public class RoleMapper {
    public RoleDto toDto(Role role) {
        if (role == null) {
            return null;
        }

        return RoleDto.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .build();
    }
}
