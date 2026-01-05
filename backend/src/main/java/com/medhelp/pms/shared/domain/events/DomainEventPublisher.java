package com.medhelp.pms.shared.domain.events;

import com.medhelp.pms.shared.domain.events.entities.DomainEventEntity;
import com.medhelp.pms.shared.domain.events.entities.repositories.DomainEventRepository;
import com.medhelp.pms.shared.infrastructure.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class DomainEventPublisher {
    private final ApplicationEventPublisher eventPublisher;
    private final DomainEventRepository eventRepository;
    private final JsonConverter jsonConverter;

    /**
     * Publish domain event (stores and publishes)
     */
    @Transactional(propagation = Propagation.MANDATORY)
    public void publish(DomainEvent event) {
        log.debug("Publishing domain event: {}", event.getEventType());

        // Set user ID if not already set
        if (event.getUserId() == null) {
            UUID currentUserId = SecurityUtils.getCurrentUserId();
            if (currentUserId != null) {
                event.setUserId(currentUserId.toString());
            }
        }

        // Check for duplicate event (idempotency)
        UUID eventId = UUID.fromString(event.getEventId());
        if (eventRepository.existsByEventId(eventId)) {
            log.warn("Event with ID {} already exists. Skipping.", eventId);
            return;
        }

        // Store event first
        DomainEventEntity entity = toEntity(event);
        eventRepository.save(entity);

        log.info("Domain event stored: {} (seq: {})",
                event.getEventType(), entity.getSequenceNumber());

        // Publish for async listeners (will be called after transaction commits)
        eventPublisher.publishEvent(event);
    }

    /**
            * Publish multiple events atomically
     */
    @Transactional(propagation = Propagation.MANDATORY)
    public void publishAll(Iterable<DomainEvent> events) {
        for (DomainEvent event : events) {
            publish(event);
        }
    }

    /**
     * After commit listener - logs successful publication
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAfterCommit(DomainEvent event) {
        log.debug("Event published after commit: {} ({})",
                event.getEventType(), event.getEventId());
    }

    /**
     * After rollback listener - logs rollback
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void handleAfterRollback(DomainEvent event) {
        log.warn("Event publication rolled back: {} ({})",
                event.getEventType(), event.getEventId());
    }

    /**
     * Convert domain event to entity
     */
    private DomainEventEntity toEntity(DomainEvent event) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("source", "pharmacy-system");
        metadata.put("environment", System.getProperty("spring.profiles.active", "development"));

        return DomainEventEntity.builder()
                .eventId(UUID.fromString(event.getEventId()))
                .eventType(event.getEventType())
                .eventVersion(event.getEventVersion())
                .aggregateType(event.getAggregateType())
                .aggregateId(UUID.fromString(event.getAggregateId()))
                .causationId(event.getCausationId() != null ?
                        UUID.fromString(event.getCausationId()) : null)
                .correlationId(event.getCorrelationId() != null ?
                        UUID.fromString(event.getCorrelationId()) : null)
                .occurredAt(event.getOccurredAt())
                .userId(event.getUserId() != null ?
                        UUID.fromString(event.getUserId()) : null)
                .eventData(jsonConverter.convertToJson(event.getData()))
                .metadata(metadata)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
