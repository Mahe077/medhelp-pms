package com.medhelp.pms.shared.domain.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public abstract class DomainEvent {

    private final String eventId;
    private final String eventType;
    private final String eventVersion;
    private final String aggregateType;
    private final String aggregateId;
    private final LocalDateTime occurredAt;

    @Setter
    private String userId;

    @Setter
    private String causationId;  // What caused this event (command ID)

    @Setter
    private String correlationId; // For tracing related events

    protected DomainEvent(
            String eventType,
            String eventVersion,
            String aggregateType,
            String aggregateId,
            String userId
    ) {
        this.eventId = UUID.randomUUID().toString();
        this.eventType = eventType;
        this.eventVersion = eventVersion;
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.occurredAt = LocalDateTime.now();
        this.userId = userId;
        this.causationId = null;
        this.correlationId = UUID.randomUUID().toString();
    }

    /**
     * Get event-specific payload data
     */
    public abstract Object getData();
}
