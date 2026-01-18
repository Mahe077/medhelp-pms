package com.medhelp.pms.modules.patient_module.domain.entities;

import com.medhelp.pms.modules.auth_module.domain.entities.User;
import com.medhelp.pms.shared.domain.entities.BaseEntity;
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
@Table(name = "patient_allergies", schema = "patient_schema")
public class PatientAllergy extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @Size(max = 50)
    @NotNull
    @Column(name = "allergen_type", nullable = false, length = 50)
    private String allergenType;

    @Size(max = 255)
    @NotNull
    @Column(name = "allergen_name", nullable = false)
    private String allergenName;

    @Size(max = 255)
    @Column(name = "reaction")
    private String reaction;

    @Size(max = 20)
    @Column(name = "severity", length = 20)
    private String severity;

    @Column(name = "notes", length = Integer.MAX_VALUE)
    private String notes;

    @Column(name = "onset_date")
    private LocalDate onsetDate;

    @ColumnDefault("true")
    @Column(name = "is_active")
    private Boolean isActive;
}