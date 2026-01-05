package com.medhelp.pms.shared.domain.events.entities.repositories;

import com.medhelp.pms.shared.domain.events.entities.DomainEventEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface DomainEventRepository extends JpaRepository<DomainEventEntity, Long> {

    /**
     * Find all events for a specific aggregate
     */
    List<DomainEventEntity> findByAggregateTypeAndAggregateIdOrderBySequenceNumberAsc(
            String aggregateType,
            UUID aggregateId
    );

    /**
     * Find events by type
     */
    List<DomainEventEntity> findByEventTypeOrderByOccurredAtDesc(String eventType);

    /**
     * Find events by correlation ID (for tracing related events)
     */
    List<DomainEventEntity> findByCorrelationIdOrderByOccurredAtAsc(UUID correlationId);

    /**
     * Find events in date range
     */
    List<DomainEventEntity> findByOccurredAtBetweenOrderByOccurredAtAsc(
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    /**
     * Find events for aggregate since a specific sequence number
     */
    @Query("""
        SELECT e FROM DomainEventEntity e 
        WHERE e.aggregateType = :aggregateType 
        AND e.aggregateId = :aggregateId 
        AND e.sequenceNumber > :afterSequence
        ORDER BY e.sequenceNumber ASC
    """)
    List<DomainEventEntity> findEventsSinceSequence(
            @Param("aggregateType") String aggregateType,
            @Param("aggregateId") UUID aggregateId,
            @Param("afterSequence") Long afterSequence
    );

    /**
     * Find recent events (for monitoring/debugging)
     */
    Page<DomainEventEntity> findAllByOrderByOccurredAtDesc(Pageable pageable);

    /**
     * Count events by type
     */
    long countByEventType(String eventType);

    /**
     * Count events for aggregate
     */
    long countByAggregateTypeAndAggregateId(String aggregateType, UUID aggregateId);

    /**
     * Find events by user
     */
    List<DomainEventEntity> findByUserIdOrderByOccurredAtDesc(UUID userId);

    /**
     * Check if event ID exists (idempotency check)
     */
    boolean existsByEventId(UUID eventId);
}
