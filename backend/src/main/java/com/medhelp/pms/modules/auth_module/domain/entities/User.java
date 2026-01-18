package com.medhelp.pms.modules.auth_module.domain.entities;

import com.medhelp.pms.shared.domain.entities.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "users", schema = "user_schema")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity implements UserDetails {

    @Column(name = "username", unique = true, nullable = false, length = 50)
    private String username;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "role", nullable = false, length = 50)
    private String role;

    @Column(name = "license_number", length = 50)
    private String licenseNumber;

    @Column(name = "user_type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private com.medhelp.pms.modules.auth_module.domain.value_objects.UserType userType;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "is_email_verified", nullable = false)
    @Builder.Default
    private Boolean isEmailVerified = false;

    @Column(name = "verification_token", length = 100)
    private String verificationToken;

    @Column(name = "verification_token_expiry")
    private LocalDateTime verificationTokenExpiry;

    @Column(name = "preferred_language", length = 10)
    @Builder.Default
    private String preferredLanguage = "en";

    @Column(name = "preferred_theme", length = 20)
    @Builder.Default
    private String preferredTheme = "system";

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Transient
    @Builder.Default
    private Set<String> permissions = new HashSet<>();

    // UserDetails implementation
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();

        // Add role as authority
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role));

        // Add permissions as authorities
        authorities.addAll(permissions.stream()
                .map(SimpleGrantedAuthority::new)
                .toList());

        return authorities;
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isActive;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isActive;
    }

    // Business methods
    public void updateLastLogin() {
        this.lastLoginAt = LocalDateTime.now();
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public boolean hasPermission(String permission) {
        return permissions.contains(permission);
    }

    public boolean hasRole(String role) {
        return this.role.equalsIgnoreCase(role);
    }
}
