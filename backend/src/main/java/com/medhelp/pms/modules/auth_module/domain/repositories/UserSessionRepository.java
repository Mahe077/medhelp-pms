package com.medhelp.pms.modules.auth_module.domain.repositories;

import com.medhelp.pms.modules.auth_module.domain.entities.User;
import com.medhelp.pms.modules.auth_module.domain.entities.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, Long> {
    Optional<UserSession> findByRefreshToken(String refreshToken);

    List<UserSession> findByUserAndRevokedAtIsNull(User user);

    @Modifying
    @Query("UPDATE UserSession rt SET rt.revokedAt = :now WHERE rt.user = :user AND rt.revokedAt IS NULL")
    void revokeAllUserTokens(User user, LocalDateTime now);

    @Modifying
    @Query("DELETE FROM UserSession rt WHERE rt.expiresAt < :now")
    void deleteExpiredTokens(LocalDateTime now);
}
