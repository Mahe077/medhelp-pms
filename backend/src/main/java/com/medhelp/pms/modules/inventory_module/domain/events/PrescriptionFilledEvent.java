package com.medhelp.pms.modules.inventory_module.domain.events;

import com.medhelp.pms.shared.domain.events.DomainEvent;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
public class PrescriptionFilledEvent extends DomainEvent {

    private final PrescriptionFilledData data;

    public PrescriptionFilledEvent(
            String prescriptionId,
            String userId,
            PrescriptionFilledData data
    ) {
        super(
                "PrescriptionFilled",
                "v1",
                "prescription",
                prescriptionId,
                userId
        );
        this.data = data;
    }

    @Override
    public PrescriptionFilledData getData() {
        return data;
    }

    @Getter
    @Builder
    public static class PrescriptionFilledData {
        private String prescriptionNumber;
        private String patientId;
        private String patientName;
        private List<FilledItem> items;
        private boolean counselingCompleted;
        private LocalDateTime filledAt;
        private String filledBy;
        private String pharmacistName;
    }

    @Getter
    @Builder
    public static class FilledItem {
        private String prescriptionItemId;
        private String medicationId;
        private String medicationName;
        private String dispensedNdc;
        private Double dispensedQuantity;
        private String batchId;
        private String batchNumber;
    }
}