package com.medhelp.pms.modules.patient_module.domain.entities;

import com.medhelp.pms.modules.auth_module.domain.entities.User;
import com.medhelp.pms.shared.domain.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "patient_conditions", schema = "patient_schema")
public class PatientCondition extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @Size(max = 255)
    @NotNull
    @Column(name = "condition_name", nullable = false)
    private String conditionName;

    @Size(max = 10)
    @Column(name = "icd_10_code", length = 10)
    private String icd10Code;

    @Column(name = "diagnosed_date")
    private LocalDate diagnosedDate;

    @ColumnDefault("true")
    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "notes", length = Integer.MAX_VALUE)
    private String notes;
}