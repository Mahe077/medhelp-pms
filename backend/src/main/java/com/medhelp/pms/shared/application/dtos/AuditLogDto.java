package com.medhelp.pms.shared.application.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogDto {
    private UUID id;
    private String eventType;
    private String description;
    private String aggregateType;
    private UUID aggregateId;
    private LocalDateTime occurredAt;
    private UUID userId;
    private String userName;
    private String eventData;
    private Long sequenceNumber;
}
