package com.medhelp.pms.modules.auth_module.domain.services;

import com.medhelp.pms.modules.auth_module.application.dtos.*;
import com.medhelp.pms.modules.auth_module.application.mappers.UserMapper;
import com.medhelp.pms.modules.auth_module.domain.entities.User;
import com.medhelp.pms.modules.auth_module.domain.entities.UserSession;
import com.medhelp.pms.modules.auth_module.domain.events.UserLoggedInEvent;
import com.medhelp.pms.modules.auth_module.domain.repositories.AuthRepository;
import com.medhelp.pms.modules.auth_module.domain.repositories.UserSessionRepository;
import com.medhelp.pms.modules.auth_module.domain.value_objects.UserType;
import com.medhelp.pms.shared.domain.events.DomainEventPublisher;
import com.medhelp.pms.shared.domain.exceptions.BusinessException;
import com.medhelp.pms.shared.infrastructure.security.JwtService;
import com.medhelp.pms.shared.infrastructure.security.SecurityUtils;
import com.medhelp.pms.shared.infrastructure.notification.EmailService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final AuthRepository authRepository;
    private final UserSessionRepository userSessionRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;
    private final EmailService emailService;
    private final DomainEventPublisher eventPublisher;

    @Value("${jwt.refresh-expiration}")
    private long refreshTokenDuration;

    /**
     * Authenticate user and generate tokens
     */
    @Transactional
    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt for user: {}", request.getUsernameOrEmail());

        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsernameOrEmail(),
                            request.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Get user
            User user = authRepository.findByUsernameOrEmail(request.getUsernameOrEmail())
                    .orElseThrow(() -> new BusinessException("User not found"));

            if (!user.getIsActive()) {
                throw new BusinessException("User account is inactive");
            }

            // Check email verification for external users
            if (user.getUserType() == UserType.EXTERNAL
                    && !user.getIsEmailVerified()) {
                // Generate new token if needed or just resend
                String verificationToken = UUID.randomUUID().toString();
                user.setVerificationToken(verificationToken);
                user.setVerificationTokenExpiry(LocalDateTime.now().plusHours(24));
                authRepository.save(user);

                String verificationLink = "http://localhost:3000/en/verify-email?token=" + verificationToken;
                emailService.sendVerificationEmail(user.getEmail(), verificationLink);

                throw new BusinessException("Email not verified. A new verification link has been sent to your email.");
            }

            // Update last login
            user.updateLastLogin();
            authRepository.save(user);

            // Generate tokens
            String accessToken = jwtService.generateAccessToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);

            // Save refresh token
            saveUserSession(user, refreshToken);

            // Get request info for event
            HttpServletRequest httpRequest = getCurrentHttpRequest();
            String ipAddress = getClientIp(httpRequest);
            String userAgent = httpRequest != null ? httpRequest.getHeader("User-Agent") : null;

            // Publish UserLoggedIn event
            UserLoggedInEvent.UserLoggedInData eventData = UserLoggedInEvent.UserLoggedInData.builder()
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .userType(user.getUserType().name())
                    .role(user.getRole())
                    .ipAddress(ipAddress)
                    .userAgent(userAgent)
                    .loginAt(LocalDateTime.now())
                    .sessionId(refreshToken.substring(0, Math.min(8, refreshToken.length())))
                    .rememberMe(false)
                    .build();

            eventPublisher.publish(new UserLoggedInEvent(
                    user.getId().toString(),
                    eventData));

            log.info("User logged in successfully: {}", user.getUsername());

            return AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .tokenType("Bearer")
                    .expiresIn(jwtService.getExpirationInSeconds())
                    .user(userMapper.toDto(user))
                    .build();

        } catch (BadCredentialsException e) {
            log.error("Invalid credentials for user: {}", request.getUsernameOrEmail());
            throw new BusinessException("Invalid username/email or password");
        }
    }

    /**
     * Refresh access token using refresh token
     */
    @Transactional
    public RefreshTokenResponse refreshToken(RefreshTokenRequest request) {
        log.info("Refresh token request received");

        String refreshTokenString = request.getRefreshToken();

        // Find refresh token in database
        UserSession userSession = userSessionRepository.findByRefreshToken(refreshTokenString)
                .orElseThrow(() -> new BusinessException("Invalid refresh token"));

        // Validate refresh token
        if (!userSession.isValid()) {
            userSessionRepository.delete(userSession);
            throw new BusinessException("Refresh token is expired or revoked");
        }

        // Get user
        User user = userSession.getUser();

        if (!user.getIsActive()) {
            throw new BusinessException("User account is inactive");
        }

        // Generate new access token
        String newAccessToken = jwtService.generateAccessToken(user);

        log.info("Access token refreshed for user: {}", user.getUsername());

        return RefreshTokenResponse.builder()
                .accessToken(newAccessToken)
                .tokenType("Bearer")
                .expiresIn(jwtService.getExpirationInSeconds())
                .build();
    }

    /**
     * Logout user - revoke refresh tokens
     */
    @Transactional
    public void logout() {
        User currentUser = SecurityUtils.getCurrentUser();

        if (currentUser == null) {
            log.warn("Logout attempt with no authenticated user");
            return;
        }

        log.info("Logout request for user: {}", currentUser.getUsername());

        // Revoke all user's refresh tokens
        userSessionRepository.revokeAllUserTokens(currentUser, LocalDateTime.now());

        // Clear security context
        SecurityContextHolder.clearContext();

        log.info("User logged out successfully: {}", currentUser.getUsername());
    }

    /**
     * Logout from all devices - revoke all refresh tokens
     */
    @Transactional
    public void logoutFromAllDevices() {
        User currentUser = SecurityUtils.getCurrentUser();

        if (currentUser == null) {
            throw new BusinessException("No authenticated user");
        }

        log.info("Logout from all devices for user: {}", currentUser.getUsername());

        userSessionRepository.revokeAllUserTokens(currentUser, LocalDateTime.now());

        log.info("User logged out from all devices: {}", currentUser.getUsername());
    }

    /**
     * Change password
     */
    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        User currentUser = SecurityUtils.getCurrentUser();

        if (currentUser == null) {
            throw new BusinessException("No authenticated user");
        }

        log.info("Change password request for user: {}", currentUser.getUsername());

        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), currentUser.getPasswordHash())) {
            throw new BusinessException("Current password is incorrect");
        }

        // Validate new password (additional rules can be added)
        if (request.getCurrentPassword().equals(request.getNewPassword())) {
            throw new BusinessException("New password must be different from current password");
        }

        // Update password
        currentUser.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        authRepository.save(currentUser);

        // Revoke all refresh tokens (force re-login on all devices)
        userSessionRepository.revokeAllUserTokens(currentUser, LocalDateTime.now());

        log.info("Password changed successfully for user: {}", currentUser.getUsername());
    }

    /**
     * Register external user
     */
    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        log.info("Registration attempt for user: {}", request.getUsername());

        if (authRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("Username already exists");
        }

        if (authRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email already exists");
        }

        String verificationToken = UUID.randomUUID().toString();

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .role("USER") // Default role for external users
                .userType(UserType.EXTERNAL)
                .isActive(true)
                .isEmailVerified(false)
                .verificationToken(verificationToken)
                .verificationTokenExpiry(LocalDateTime.now().plusHours(24))
                .build();

        User savedUser = authRepository.save(user);

        String verificationLink = "http://localhost:3000/en/verify-email?token=" + verificationToken;
        emailService.sendVerificationEmail(savedUser.getEmail(), verificationLink);

        log.info("User registered successfully: {}. Verification token: {}", savedUser.getUsername(),
                verificationToken);

        return RegisterResponse.builder()
                .message("Registration successful. Please check your email for verification link.")
                .user(userMapper.toDto(savedUser))
                .build();
    }

    /**
     * Verify user email
     */
    @Transactional
    public void verifyEmail(String token) {
        log.info("Email verification attempt with token: {}", token);

        User user = authRepository.findByVerificationToken(token)
                .orElseThrow(() -> new BusinessException("Invalid or expired verification token"));

        if (user.getVerificationTokenExpiry() != null
                && user.getVerificationTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new BusinessException("Verification link expired");
        }

        user.setIsEmailVerified(true);
        user.setVerificationToken(null);
        user.setVerificationTokenExpiry(null);
        authRepository.save(user);

        log.info("Email verified successfully for user: {}", user.getUsername());
    }

    /**
     * Resend verification email
     */
    @Transactional
    public void resendVerificationEmail(String email) {
        User user = authRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("User not found with email: " + email));

        if (user.getIsEmailVerified()) {
            throw new BusinessException("Email is already verified");
        }

        String verificationToken = UUID.randomUUID().toString();
        user.setVerificationToken(verificationToken);
        user.setVerificationTokenExpiry(LocalDateTime.now().plusHours(24));
        authRepository.save(user);

        String verificationLink = "http://localhost:3000/en/verify-email?token=" + verificationToken;
        emailService.sendVerificationEmail(user.getEmail(), verificationLink);

        log.info("Verification email resent to: {}", email);
    }

    /**
     * Get current authenticated user
     */
    @Transactional(readOnly = true)
    public UserDto getCurrentUser() {
        User currentUser = (User) org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        // Refresh from DB to get latest permissions/data
        User user = authRepository.findById(currentUser.getId())
                .orElseThrow(() -> new BusinessException("User not found"));

        return userMapper.toDto(user);
    }

    /**
     * Update current user's preferences
     */
    @Transactional
    public UserDto updatePreferences(UserPreferencesRequest request) {
        User currentUser = (User) org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        User user = authRepository.findById(currentUser.getId())
                .orElseThrow(() -> new BusinessException("User not found"));

        if (request.getPreferredLanguage() != null) {
            user.setPreferredLanguage(request.getPreferredLanguage());
        }
        if (request.getPreferredTheme() != null) {
            user.setPreferredTheme(request.getPreferredTheme());
        }

        return userMapper.toDto(authRepository.save(user));
    }

    /**
     * Validate access token
     */
    public boolean validateToken(String token) {
        try {
            String username = jwtService.extractUsername(token);
            User user = authRepository.findByUsername(username)
                    .orElseThrow(() -> new BusinessException("User not found"));

            return jwtService.isTokenValid(token, user);
        } catch (Exception e) {
            log.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Save refresh token to database
     */
    private void saveUserSession(User user, String token) {
        HttpServletRequest request = getCurrentHttpRequest();

        UserSession userSession = UserSession.builder()
                .user(user)
                .refreshToken(token)
                .expiresAt(LocalDateTime.now().plusSeconds(refreshTokenDuration / 1000))
                .ipAddress(getClientIp(request))
                .userAgent(request != null ? request.getHeader("User-Agent") : null)
                .build();

        userSessionRepository.save(userSession);
    }

    /**
     * Get current HTTP request
     */
    private HttpServletRequest getCurrentHttpRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }

    /**
     * Get client IP address
     */
    private String getClientIp(HttpServletRequest request) {
        if (request == null) {
            return null;
        }

        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        return ip;
    }

    /**
     * Clean up expired refresh tokens (scheduled task)
     */
    @Transactional
    public void cleanupExpiredTokens() {
        log.info("Cleaning up expired refresh tokens");
        userSessionRepository.deleteExpiredTokens(LocalDateTime.now());
    }
}