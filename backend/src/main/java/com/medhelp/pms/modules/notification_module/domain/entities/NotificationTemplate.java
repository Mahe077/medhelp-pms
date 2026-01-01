package com.medhelp.pms.modules.notification_module.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "notification_templates", schema = "notification_schema")
public class NotificationTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Size(max = 100)
    @NotNull
    @Column(name = "template_name", nullable = false, length = 100)
    private String templateName;

    @Size(max = 50)
    @NotNull
    @Column(name = "notification_type", nullable = false, length = 50)
    private String notificationType;

    @Size(max = 100)
    @NotNull
    @Column(name = "event_trigger", nullable = false, length = 100)
    private String eventTrigger;

    @Size(max = 255)
    @Column(name = "subject")
    private String subject;

    @NotNull
    @Column(name = "body_template", nullable = false, length = Integer.MAX_VALUE)
    private String bodyTemplate;

    @ColumnDefault("true")
    @Column(name = "is_active")
    private Boolean isActive;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

}