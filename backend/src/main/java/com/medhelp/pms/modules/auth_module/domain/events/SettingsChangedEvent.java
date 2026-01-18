package com.medhelp.pms.modules.auth_module.domain.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SettingsChangedEvent {
    private UUID userId;
    private String settingCategory;
    private String settingName;
    private String oldValue;
    private String newValue;
    private LocalDateTime changedAt;
    private UUID changedBy;
}
