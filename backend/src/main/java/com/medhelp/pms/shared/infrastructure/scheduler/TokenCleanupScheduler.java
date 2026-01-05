package com.medhelp.pms.shared.infrastructure.scheduler;

import com.medhelp.pms.modules.auth_module.domain.services.AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class TokenCleanupScheduler {
    private final AuthenticationService authenticationService;

    /**
     * Clean up expired tokens every day at 2 AM
     */
    @Scheduled(cron = "0 0 2 * * *")
    public void cleanupExpiredTokens() {
        log.info("Running scheduled task: cleanup expired refresh tokens");
        authenticationService.cleanupExpiredTokens();
        log.info("Finished scheduled task: cleanup expired refresh tokens");
    }
}
