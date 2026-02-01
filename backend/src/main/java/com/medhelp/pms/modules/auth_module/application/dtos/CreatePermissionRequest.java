package com.medhelp.pms.modules.auth_module.application.dtos;

import lombok.Data;

@Data
public class CreatePermissionRequest {
    private String name;
    private String description;
    private String resource;
    private String action;
}
