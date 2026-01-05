package com.medhelp.pms.shared.domain.events.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(
        name = "domain_events",
        schema = "audit_schema",
        indexes = {
                @Index(name = "idx_domain_events_aggregate", columnList = "aggregate_type,aggregate_id"),
                @Index(name = "idx_domain_events_type", columnList = "event_type"),
                @Index(name = "idx_domain_events_occurred_at", columnList = "occurred_at"),
                @Index(name = "idx_domain_events_correlation", columnList = "correlation_id"),
                @Index(name = "idx_domain_events_user", columnList = "user_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DomainEventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sequence_number")
    private Long sequenceNumber;

    @Column(name = "event_id", nullable = false, unique = true)
    private UUID eventId;

    @Column(name = "event_type", nullable = false, length = 100)
    private String eventType;

    @Column(name = "event_version", nullable = false, length = 10)
    private String eventVersion;

    @Column(name = "aggregate_type", nullable = false, length = 50)
    private String aggregateType;

    @Column(name = "aggregate_id", nullable = false)
    private UUID aggregateId;

    @Column(name = "causation_id")
    private UUID causationId;

    @Column(name = "correlation_id")
    private UUID correlationId;

    @Column(name = "occurred_at", nullable = false)
    private LocalDateTime occurredAt;

    @Column(name = "user_id")
    private UUID userId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "event_data", nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> eventData;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata", columnDefinition = "jsonb")
    private Map<String, Object> metadata;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}