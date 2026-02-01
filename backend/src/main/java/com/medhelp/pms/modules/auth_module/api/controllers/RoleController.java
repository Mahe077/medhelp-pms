package com.medhelp.pms.modules.auth_module.api.controllers;

import com.medhelp.pms.modules.auth_module.application.dtos.CreateRoleRequest;
import com.medhelp.pms.modules.auth_module.domain.entities.Role;
import com.medhelp.pms.modules.auth_module.domain.services.RoleService;
import com.medhelp.pms.shared.api.validators.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/access/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Role>>> getAllRoles() {
        return ResponseEntity.ok(ApiResponse.success(roleService.getAllRoles()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Role>> getRole(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(roleService.getRoleById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Role>> createRole(@RequestBody CreateRoleRequest request) {
        return ResponseEntity
                .ok(ApiResponse.success(roleService.createRole(request.getName(), request.getDescription())));
    }
}
