package com.medhelp.pms.modules.auth_module.application.dtos;

import lombok.Data;
import java.util.UUID;

@Data
public class AssignPermissionRequest {
    private UUID permissionId;
}
