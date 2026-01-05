package com.medhelp.pms.shared.domain.events.services;

import com.medhelp.pms.modules.inventory_module.domain.events.PrescriptionFilledEvent;
import com.medhelp.pms.modules.prescription_module.domain.PrescriptionReceivedEvent;
import com.medhelp.pms.shared.domain.events.JsonConverter;
import com.medhelp.pms.shared.domain.events.entities.DomainEventEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventDeserializer {

    private final JsonConverter jsonConverter;

    /**
     * Deserialize event entity back to domain event
     */
    public Object deserialize(DomainEventEntity entity) {
        String eventType = entity.getEventType();
        Map<String, Object> eventData = entity.getEventData();

        try {
            return switch (eventType) {
                case "PrescriptionFilled" -> deserializePrescriptionFilled(entity, eventData);
                case "PrescriptionReceived" -> deserializePrescriptionReceived(entity, eventData);
                case "StockLevelChanged" -> deserializeStockLevelChanged(entity, eventData);
                // Add more event types here
                default -> {
                    log.warn("Unknown event type for deserialization: {}", eventType);
                    yield null;
                }
            };
        } catch (Exception e) {
            log.error("Failed to deserialize event: {} ({})",
                    eventType,
                    entity.getEventId(),
                    e);
            throw new RuntimeException("Event deserialization failed", e);
        }
    }

    private PrescriptionFilledEvent deserializePrescriptionFilled(
            DomainEventEntity entity,
            Map<String, Object> eventData
    ) {
        PrescriptionFilledEvent.PrescriptionFilledData data =
                jsonConverter.convertFromJson(
                        eventData,
                        PrescriptionFilledEvent.PrescriptionFilledData.class
                );

        return new PrescriptionFilledEvent(
                entity.getAggregateId().toString(),
                entity.getUserId() != null ? entity.getUserId().toString() : null,
                data
        );
    }

    private PrescriptionReceivedEvent deserializePrescriptionReceived(
            DomainEventEntity entity,
            Map<String, Object> eventData
    ) {
        // Similar implementation
        return null; // TODO: Implement
    }

    private StockLevelChangedEvent deserializeStockLevelChanged(
            DomainEventEntity entity,
            Map<String, Object> eventData
    ) {
        // Similar implementation
        return null; // TODO: Implement
    }
}