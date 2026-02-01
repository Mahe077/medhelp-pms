package com.medhelp.pms.modules.auth_module.domain.events;

import com.medhelp.pms.shared.domain.events.DomainEvent;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Domain event published when a user successfully logs in.
 * This event is used for audit logging and can trigger downstream processes
 * like updating last login timestamps or sending security notifications.
 */
@Getter
public class UserLoggedInEvent extends DomainEvent {

    private final UserLoggedInData data;

    public UserLoggedInEvent(
            String userId,
            UserLoggedInData data) {
        super(
                "UserLoggedIn",
                "v1",
                "user",
                userId,
                userId);
        this.data = data;
    }

    @Override
    public UserLoggedInData getData() {
        return data;
    }

    @Getter
    @Builder
    public static class UserLoggedInData {
        private String username;
        private String email;
        private String userType;
        private Set<String> roles;
        private String ipAddress;
        private String userAgent;
        private LocalDateTime loginAt;
        private String sessionId;
        private boolean rememberMe;
    }
}
