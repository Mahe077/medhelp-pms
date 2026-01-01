package com.medhelp.pms.modules.prescription_module.domain.entities;

import com.medhelp.pms.shared.domain.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@Table(name = "drug_interactions", schema = "prescription_schema")
public class DrugInteraction extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medication_1_id")
    private Medication medication1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medication_2_id")
    private Medication medication2;

    @Size(max = 50)
    @Column(name = "interaction_type", length = 50)
    private String interactionType;

    @NotNull
    @Column(name = "description", nullable = false, length = Integer.MAX_VALUE)
    private String description;

    @Size(max = 20)
    @NotNull
    @Column(name = "severity", nullable = false, length = 20)
    private String severity;

    @Column(name = "management_recommendation", length = Integer.MAX_VALUE)
    private String managementRecommendation;

    @ColumnDefault("true")
    @Column(name = "is_active")
    private Boolean isActive;
}