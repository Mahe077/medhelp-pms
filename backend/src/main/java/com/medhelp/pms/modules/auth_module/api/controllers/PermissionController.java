package com.medhelp.pms.modules.auth_module.api.controllers;

import com.medhelp.pms.modules.auth_module.application.dtos.CreatePermissionRequest;
import com.medhelp.pms.modules.auth_module.domain.entities.Permission;
import com.medhelp.pms.modules.auth_module.domain.services.PermissionService;
import com.medhelp.pms.shared.api.validators.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/access/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Permission>>> getAllPermissions() {
        return ResponseEntity.ok(ApiResponse.success(permissionService.getAllPermissions()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Permission>> createPermission(@RequestBody CreatePermissionRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success(permissionService.createPermission(
                        request.getName(),
                        request.getDescription(),
                        request.getResource(),
                        request.getAction())));
    }
}
