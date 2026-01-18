package com.medhelp.pms.modules.auth_module.domain.repositories;

import com.medhelp.pms.modules.auth_module.domain.entities.UserNotificationPreferences;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserNotificationPreferencesRepository extends JpaRepository<UserNotificationPreferences, UUID> {

    @Query("SELECT unp FROM UserNotificationPreferences unp WHERE unp.userId = :userId AND unp.deletedAt IS NULL")
    Optional<UserNotificationPreferences> findByUserId(@Param("userId") UUID userId);

    @Query("SELECT CASE WHEN COUNT(unp) > 0 THEN true ELSE false END FROM UserNotificationPreferences unp WHERE unp.userId = :userId AND unp.deletedAt IS NULL")
    boolean existsByUserId(@Param("userId") UUID userId);
}
