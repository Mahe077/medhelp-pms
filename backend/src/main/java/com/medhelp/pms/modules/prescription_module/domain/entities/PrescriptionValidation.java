package com.medhelp.pms.modules.prescription_module.domain.entities;

import com.medhelp.pms.modules.auth_module.domain.entities.User;
import com.medhelp.pms.shared.domain.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@Entity
@Table(name = "prescription_validations", schema = "prescription_schema")
public class PrescriptionValidation extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "prescription_id")
    private Prescription prescription;

    @Size(max = 50)
    @NotNull
    @Column(name = "validation_type", nullable = false, length = 50)
    private String validationType;

    @Size(max = 20)
    @NotNull
    @Column(name = "validation_status", nullable = false, length = 20)
    private String validationStatus;

    @Column(name = "details")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> details;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "performed_at")
    private LocalDateTime performedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performed_by")
    private User performedBy;

}