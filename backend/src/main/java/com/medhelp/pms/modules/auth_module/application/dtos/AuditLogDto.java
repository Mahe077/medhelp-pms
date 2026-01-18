package com.medhelp.pms.modules.auth_module.application.dtos;

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
    private String aggregateType;
    private UUID aggregateId;
    private String eventData;
    private LocalDateTime occurredAt;
    private UUID userId;
    private String userName;
    private Long sequenceNumber;
}
