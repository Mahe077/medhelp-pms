package com.medhelp.pms.modules.auth_module.api.controllers;

import com.medhelp.pms.modules.auth_module.application.dtos.*;
import com.medhelp.pms.modules.auth_module.domain.services.AuthenticationService;
import com.medhelp.pms.shared.api.validators.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationService authService;

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Authenticate user and get access token")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody LoginRequest loginRequest) {
        AuthResponse authResponse = authService.login(loginRequest);
        return ResponseEntity.ok(ApiResponse.success(authResponse));
    }

    @PostMapping("/register")
    @Operation(summary = "Register", description = "Register a new external user")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(@RequestBody RegisterRequest registerRequest) {
        RegisterResponse response = authService.register(registerRequest);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh token", description = "Get new access token using refresh token")
    public ResponseEntity<ApiResponse<RefreshTokenResponse>> refreshToken(
            @RequestBody @Valid RefreshTokenRequest request
    ) {
        RefreshTokenResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout", description = "Logout current user and revoke refresh token")
    public ResponseEntity<ApiResponse<Void>> logout() {
        authService.logout();
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/logout-all")
    @Operation(summary = "Logout from all devices", description = "Revoke all refresh tokens for current user")
    public ResponseEntity<ApiResponse<Void>> logoutFromAllDevices() {
        authService.logoutFromAllDevices();
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/change-password")
    @Operation(summary = "Change password", description = "Change password for current user")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @RequestBody @Valid ChangePasswordRequest request
    ) {
        authService.changePassword(request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/verify-email")
    @Operation(summary = "Verify Email", description = "Verify user email using token")
    public ResponseEntity<ApiResponse<Void>> verifyEmail(@RequestParam("token") String token) {
        authService.verifyEmail(token);
        return ResponseEntity.ok(ApiResponse.success(null, "Email verified successfully"));
    }

    @PostMapping("/resend-verification")
    @Operation(summary = "Resend Verification Email", description = "Resend verification email to user")
    public ResponseEntity<ApiResponse<Void>> resendVerification(@RequestParam("email") String email) {
        authService.resendVerificationEmail(email);
        return ResponseEntity.ok(ApiResponse.success(null, "Verification email sent successfully"));
    }

    @GetMapping("/me")
    @Operation(summary = "Get Current User", description = "Get profile of the currently authenticated user")
    public ResponseEntity<ApiResponse<UserDto>> getCurrentUser() {
        return ResponseEntity.ok(ApiResponse.success(authService.getCurrentUser()));
    }

    @PatchMapping("/preferences")
    @Operation(summary = "Update Preferences", description = "Update current user's language and theme preferences")
    public ResponseEntity<ApiResponse<UserDto>> updatePreferences(@RequestBody UserPreferencesRequest request) {
        return ResponseEntity.ok(ApiResponse.success(authService.updatePreferences(request)));
    }
}
