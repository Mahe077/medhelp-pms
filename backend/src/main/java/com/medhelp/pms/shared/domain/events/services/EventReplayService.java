package com.medhelp.pms.shared.domain.events.services;

import com.medhelp.pms.shared.domain.events.entities.DomainEventEntity;
import com.medhelp.pms.shared.domain.events.entities.repositories.DomainEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventReplayService {

    private final DomainEventRepository eventRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final EventDeserializer eventDeserializer;

    /**
     * Replay all events for an aggregate (to rebuild state)
     */
    @Transactional(readOnly = true)
    public void replayAggregateEvents(String aggregateType, UUID aggregateId) {
        log.info("Replaying events for aggregate: {} - {}", aggregateType, aggregateId);

        List<DomainEventEntity> events = eventRepository
                .findByAggregateTypeAndAggregateIdOrderBySequenceNumberAsc(
                        aggregateType,
                        aggregateId
                );

        log.info("Found {} events to replay", events.size());

        for (DomainEventEntity eventEntity : events) {
            try {
                Object domainEvent = eventDeserializer.deserialize(eventEntity);
                eventPublisher.publishEvent(domainEvent);

                log.debug("Replayed event: {} (seq: {})",
                        eventEntity.getEventType(),
                        eventEntity.getSequenceNumber());
            } catch (Exception e) {
                log.error("Failed to replay event: {} (seq: {})",
                        eventEntity.getEventType(),
                        eventEntity.getSequenceNumber(),
                        e);
            }
        }

        log.info("Completed replaying events for aggregate: {} - {}",
                aggregateType,
                aggregateId);
    }

    /**
     * Replay events by type (for rebuilding projections)
     */
    @Transactional(readOnly = true)
    public void replayEventsByType(String eventType) {
        log.info("Replaying all events of type: {}", eventType);

        List<DomainEventEntity> events = eventRepository
                .findByEventTypeOrderByOccurredAtDesc(eventType);

        log.info("Found {} events to replay", events.size());

        for (DomainEventEntity eventEntity : events) {
            try {
                Object domainEvent = eventDeserializer.deserialize(eventEntity);
                eventPublisher.publishEvent(domainEvent);
            } catch (Exception e) {
                log.error("Failed to replay event: {}", eventEntity.getEventId(), e);
            }
        }

        log.info("Completed replaying events of type: {}", eventType);
    }
}