package com.medhelp.pms.modules.auth_module.api.controllers;

import com.medhelp.pms.modules.auth_module.application.dtos.UserDto;
import com.medhelp.pms.modules.auth_module.application.mappers.UserMapper;
import com.medhelp.pms.modules.auth_module.domain.entities.User;
import com.medhelp.pms.modules.auth_module.domain.services.UserService;
import com.medhelp.pms.shared.api.validators.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/access/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserDto>>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success(
                users.stream().map(userMapper::toDto).collect(Collectors.toList())));
    }

    @PostMapping("/{userId}/roles/{roleId}")
    public ResponseEntity<ApiResponse<Void>> assignRole(@PathVariable UUID userId, @PathVariable UUID roleId) {
        userService.assignRoleToUser(userId, roleId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @DeleteMapping("/{userId}/roles/{roleId}")
    public ResponseEntity<ApiResponse<Void>> removeRole(@PathVariable UUID userId, @PathVariable UUID roleId) {
        userService.removeRoleFromUser(userId, roleId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
