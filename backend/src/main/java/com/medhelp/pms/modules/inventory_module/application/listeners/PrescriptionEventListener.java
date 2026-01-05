package com.medhelp.pms.modules.inventory_module.application.listeners;

import com.medhelp.pms.modules.inventory_module.domain.events.PrescriptionFilledEvent;
import com.medhelp.pms.modules.inventory_module.domain.services.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class PrescriptionEventListener {

    private final InventoryService inventoryService;

    @EventListener
    @Async
    @Transactional
    public void handlePrescriptionFilled(PrescriptionFilledEvent event) {
        log.info("Handling PrescriptionFilled event: {}", event.getEventId());

        try {
            inventoryService.deductStock(event.getData().getItems());
        } catch (Exception e) {
            log.error("Failed to deduct stock for prescription: {}",
                    event.getAggregateId(), e);
            // Could publish a compensation event or retry
        }
    }
}