package com.medhelp.pms.modules.auth_module.application.dtos;

import lombok.Data;

@Data
public class CreateRoleRequest {
    private String name;
    private String description;
}
