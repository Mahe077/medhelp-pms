package com.medhelp.pms.shared.domain.events.services;

import com.medhelp.pms.shared.domain.events.entities.DomainEventEntity;
import com.medhelp.pms.shared.domain.events.entities.repositories.DomainEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventStoreService {

    private final DomainEventRepository eventRepository;

    /**
     * Get all events for an aggregate (for event sourcing)
     */
    @Transactional(readOnly = true)
    public List<DomainEventEntity> getAggregateEvents(
            String aggregateType,
            UUID aggregateId
    ) {
        return eventRepository.findByAggregateTypeAndAggregateIdOrderBySequenceNumberAsc(
                aggregateType,
                aggregateId
        );
    }

    /**
     * Get events since a specific sequence (for incremental rebuild)
     */
    @Transactional(readOnly = true)
    public List<DomainEventEntity> getEventsSinceSequence(
            String aggregateType,
            UUID aggregateId,
            Long afterSequence
    ) {
        return eventRepository.findEventsSinceSequence(
                aggregateType,
                aggregateId,
                afterSequence
        );
    }

    /**
     * Get events by type (for analytics)
     */
    @Transactional(readOnly = true)
    public List<DomainEventEntity> getEventsByType(String eventType) {
        return eventRepository.findByEventTypeOrderByOccurredAtDesc(eventType);
    }

    /**
     * Get events in date range
     */
    @Transactional(readOnly = true)
    public List<DomainEventEntity> getEventsInDateRange(
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {
        return eventRepository.findByOccurredAtBetweenOrderByOccurredAtAsc(
                startDate,
                endDate
        );
    }

    /**
     * Get related events by correlation ID
     */
    @Transactional(readOnly = true)
    public List<DomainEventEntity> getCorrelatedEvents(UUID correlationId) {
        return eventRepository.findByCorrelationIdOrderByOccurredAtAsc(correlationId);
    }

    /**
     * Get recent events (for monitoring)
     */
    @Transactional(readOnly = true)
    public Page<DomainEventEntity> getRecentEvents(int page, int size) {
        PageRequest pageRequest = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "occurredAt")
        );
        return eventRepository.findAllByOrderByOccurredAtDesc(pageRequest);
    }

    /**
     * Get event statistics
     */
    @Transactional(readOnly = true)
    public EventStatistics getStatistics() {
        long totalEvents = eventRepository.count();

        return EventStatistics.builder()
                .totalEvents(totalEvents)
                .prescriptionFilledCount(eventRepository.countByEventType("PrescriptionFilled"))
                .prescriptionReceivedCount(eventRepository.countByEventType("PrescriptionReceived"))
                .stockLevelChangedCount(eventRepository.countByEventType("StockLevelChanged"))
                .build();
    }

    /**
     * Check event exists (for idempotency)
     */
    @Transactional(readOnly = true)
    public boolean eventExists(UUID eventId) {
        return eventRepository.existsByEventId(eventId);
    }

    @lombok.Data
    @lombok.Builder
    public static class EventStatistics {
        private long totalEvents;
        private long prescriptionFilledCount;
        private long prescriptionReceivedCount;
        private long stockLevelChangedCount;
    }
}