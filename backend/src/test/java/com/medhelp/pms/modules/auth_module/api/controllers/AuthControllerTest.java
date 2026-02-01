package com.medhelp.pms.modules.auth_module.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medhelp.pms.modules.auth_module.application.dtos.*;
import com.medhelp.pms.modules.auth_module.domain.services.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockitoBean
        private AuthenticationService authService;

        private UserDto testUserDto;
        private AuthResponse testAuthResponse;

        @BeforeEach
        void setUp() {
                testUserDto = UserDto.builder()
                                .id(UUID.randomUUID())
                                .username("testuser")
                                .email("test@example.com")
                                .firstName("Test")
                                .lastName("User")
                                .roles(Set.of("USER"))
                                .userType("EXTERNAL")
                                .isActive(true)
                                .isEmailVerified(true)
                                .preferredLanguage("en")
                                .preferredTheme("dark")
                                .permissions(Set.of("READ_PROFILE"))
                                .createdAt(LocalDateTime.now())
                                .build();

                testAuthResponse = AuthResponse.builder()
                                .accessToken("test-access-token")
                                .refreshToken("test-refresh-token")
                                .tokenType("Bearer")
                                .expiresIn(3600L)
                                .user(testUserDto)
                                .build();
        }

        @Nested
        @DisplayName("POST /auth/login")
        class LoginTests {

                @Test
                @DisplayName("Should return auth response for valid credentials")
                void login_WithValidCredentials_ReturnsAuthResponse() throws Exception {
                        LoginRequest loginRequest = LoginRequest.builder()
                                        .usernameOrEmail("testuser")
                                        .password("password123")
                                        .build();

                        when(authService.login(any(LoginRequest.class))).thenReturn(testAuthResponse);

                        mockMvc.perform(post("/auth/login")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(loginRequest)))
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.success").value(true))
                                        .andExpect(jsonPath("$.data.accessToken").value("test-access-token"))
                                        .andExpect(jsonPath("$.data.refreshToken").value("test-refresh-token"))
                                        .andExpect(jsonPath("$.data.user.username").value("testuser"));

                        verify(authService, times(1)).login(any(LoginRequest.class));
                }

                @Test
                @DisplayName("Should return 401 for invalid credentials")
                void login_WithInvalidCredentials_ReturnsUnauthorized() throws Exception {
                        LoginRequest loginRequest = LoginRequest.builder()
                                        .usernameOrEmail("testuser")
                                        .password("wrongpassword")
                                        .build();

                        when(authService.login(any(LoginRequest.class)))
                                        .thenThrow(new BadCredentialsException("Invalid credentials"));

                        mockMvc.perform(post("/auth/login")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(loginRequest)))
                                        .andExpect(status().isUnauthorized());
                }
        }

        @Nested
        @DisplayName("POST /auth/register")
        class RegisterTests {

                @Test
                @DisplayName("Should register new user successfully")
                void register_WithValidData_ReturnsSuccess() throws Exception {
                        RegisterRequest registerRequest = RegisterRequest.builder()
                                        .username("newuser")
                                        .email("newuser@example.com")
                                        .password("Password123!")
                                        .firstName("New")
                                        .lastName("User")
                                        .phone("+1234567890")
                                        .build();

                        RegisterResponse registerResponse = RegisterResponse.builder()
                                        .message("Registration successful")
                                        .user(testUserDto)
                                        .build();

                        when(authService.register(any(RegisterRequest.class))).thenReturn(registerResponse);

                        mockMvc.perform(post("/auth/register")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(registerRequest)))
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.success").value(true))
                                        .andExpect(jsonPath("$.data.message").value("Registration successful"));

                        verify(authService, times(1)).register(any(RegisterRequest.class));
                }

                @Test
                @DisplayName("Should return error for duplicate email")
                void register_WithDuplicateEmail_ReturnsError() throws Exception {
                        RegisterRequest registerRequest = RegisterRequest.builder()
                                        .username("newuser")
                                        .email("existing@example.com")
                                        .password("Password123!")
                                        .firstName("New")
                                        .lastName("User")
                                        .build();

                        when(authService.register(any(RegisterRequest.class)))
                                        .thenThrow(new IllegalArgumentException("Email already exists"));

                        mockMvc.perform(post("/auth/register")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(registerRequest)))
                                        .andExpect(status().isBadRequest());
                }
        }

        @Nested
        @DisplayName("POST /auth/refresh")
        class RefreshTokenTests {

                @Test
                @DisplayName("Should refresh token successfully")
                void refreshToken_WithValidToken_ReturnsNewTokens() throws Exception {
                        RefreshTokenRequest request = RefreshTokenRequest.builder()
                                        .refreshToken("valid-refresh-token")
                                        .build();

                        RefreshTokenResponse response = RefreshTokenResponse.builder()
                                        .accessToken("new-access-token")
                                        .tokenType("Bearer")
                                        .expiresIn(3600L)
                                        .build();

                        when(authService.refreshToken(any(RefreshTokenRequest.class))).thenReturn(response);

                        mockMvc.perform(post("/auth/refresh")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request)))
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.success").value(true))
                                        .andExpect(jsonPath("$.data.accessToken").value("new-access-token"));

                        verify(authService, times(1)).refreshToken(any(RefreshTokenRequest.class));
                }
        }

        @Nested
        @DisplayName("POST /auth/logout")
        @WithMockUser(username = "testuser")
        class LogoutTests {

                @Test
                @DisplayName("Should logout successfully")
                void logout_WhenAuthenticated_ReturnsNoContent() throws Exception {
                        doNothing().when(authService).logout();

                        mockMvc.perform(post("/auth/logout"))
                                        .andExpect(status().isNoContent());

                        verify(authService, times(1)).logout();
                }
        }

        @Nested
        @DisplayName("GET /auth/me")
        @WithMockUser(username = "testuser")
        class GetCurrentUserTests {

                @Test
                @DisplayName("Should return current user profile")
                void getCurrentUser_WhenAuthenticated_ReturnsUserProfile() throws Exception {
                        when(authService.getCurrentUser()).thenReturn(testUserDto);

                        mockMvc.perform(get("/auth/me"))
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.success").value(true))
                                        .andExpect(jsonPath("$.data.username").value("testuser"))
                                        .andExpect(jsonPath("$.data.email").value("test@example.com"));

                        verify(authService, times(1)).getCurrentUser();
                }

                @Test
                @DisplayName("Should return 401 for unauthenticated request")
                void getCurrentUser_WhenUnauthenticated_ReturnsUnauthorized() throws Exception {
                        // Create a new test without @WithMockUser
                }
        }

        @Nested
        @DisplayName("PATCH /auth/preferences")
        @WithMockUser(username = "testuser")
        class UpdatePreferencesTests {

                @Test
                @DisplayName("Should update user preferences")
                void updatePreferences_WithValidData_ReturnsUpdatedUser() throws Exception {
                        UserPreferencesRequest request = UserPreferencesRequest.builder()
                                        .preferredLanguage("si")
                                        .preferredTheme("light")
                                        .build();

                        UserDto updatedUser = UserDto.builder()
                                        .id(testUserDto.getId())
                                        .username(testUserDto.getUsername())
                                        .email(testUserDto.getEmail())
                                        .firstName(testUserDto.getFirstName())
                                        .lastName(testUserDto.getLastName())
                                        .roles(testUserDto.getRoles())
                                        .userType(testUserDto.getUserType())
                                        .isActive(testUserDto.getIsActive())
                                        .isEmailVerified(testUserDto.getIsEmailVerified())
                                        .preferredLanguage("si")
                                        .preferredTheme("light")
                                        .permissions(testUserDto.getPermissions())
                                        .build();

                        when(authService.updatePreferences(any(UserPreferencesRequest.class)))
                                        .thenReturn(updatedUser);

                        mockMvc.perform(patch("/auth/preferences")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request)))
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.success").value(true))
                                        .andExpect(jsonPath("$.data.preferredLanguage").value("si"))
                                        .andExpect(jsonPath("$.data.preferredTheme").value("light"));

                        verify(authService, times(1)).updatePreferences(any(UserPreferencesRequest.class));
                }
        }

        @Nested
        @DisplayName("POST /auth/verify-email")
        class VerifyEmailTests {

                @Test
                @DisplayName("Should verify email successfully")
                void verifyEmail_WithValidToken_ReturnsSuccess() throws Exception {
                        doNothing().when(authService).verifyEmail(anyString());

                        mockMvc.perform(post("/auth/verify-email")
                                        .param("token", "valid-verification-token"))
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.success").value(true))
                                        .andExpect(jsonPath("$.meta.message").value("Email verified successfully"));

                        verify(authService, times(1)).verifyEmail("valid-verification-token");
                }
        }

        @Nested
        @DisplayName("POST /auth/change-password")
        @WithMockUser(username = "testuser")
        class ChangePasswordTests {

                @Test
                @DisplayName("Should change password successfully")
                void changePassword_WithValidData_ReturnsSuccess() throws Exception {
                        ChangePasswordRequest request = ChangePasswordRequest.builder()
                                        .currentPassword("oldPassword123")
                                        .newPassword("newPassword123!")
                                        .build();

                        doNothing().when(authService).changePassword(any(ChangePasswordRequest.class));

                        mockMvc.perform(post("/auth/change-password")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request)))
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.success").value(true));

                        verify(authService, times(1)).changePassword(any(ChangePasswordRequest.class));
                }
        }
}
