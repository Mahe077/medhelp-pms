package com.medhelp.pms.modules.auth_module.domain.repositories;

import com.medhelp.pms.shared.application.dtos.AuditLogDto;
import com.medhelp.pms.shared.domain.events.entities.DomainEventEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SettingsAuditRepository extends JpaRepository<DomainEventEntity, Long> {

    @Query("""
            SELECT new com.medhelp.pms.shared.application.dtos.AuditLogDto(
                de.eventId,
                de.eventType,
                de.eventType,
                de.aggregateType,
                de.aggregateId,
                de.occurredAt,
                de.userId,
                CONCAT(u.firstName, ' ', u.lastName),
                CAST(de.eventData AS string),
                de.sequenceNumber
            )
                    FROM DomainEventEntity de
                    LEFT JOIN User u ON u.id = de.userId
                    WHERE de.aggregateId = :userId
                    AND (de.eventType LIKE '%Settings%' OR de.aggregateType = 'User')
                    ORDER BY de.occurredAt DESC
                """)
    Page<AuditLogDto> findUserSettingsAuditTrail(@Param("userId") UUID userId, Pageable pageable);
}
