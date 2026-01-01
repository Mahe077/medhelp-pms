package com.medhelp.pms.modules.prescription_module.domain.entities;

import com.medhelp.pms.shared.domain.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "medications", schema = "prescription_schema")
public class Medication extends BaseEntity {
    @Size(max = 11)
    @NotNull
    @Column(name = "ndc_code", nullable = false, length = 11)
    private String ndcCode;

    @Size(max = 255)
    @NotNull
    @Column(name = "drug_name", nullable = false)
    private String drugName;

    @Size(max = 255)
    @Column(name = "generic_name")
    private String genericName;

    @Size(max = 255)
    @Column(name = "brand_name")
    private String brandName;

    @Size(max = 50)
    @Column(name = "strength", length = 50)
    private String strength;

    @Size(max = 50)
    @Column(name = "dosage_form", length = 50)
    private String dosageForm;

    @Size(max = 50)
    @Column(name = "route", length = 50)
    private String route;

    @Size(max = 255)
    @Column(name = "manufacturer")
    private String manufacturer;

    @ColumnDefault("false")
    @Column(name = "is_controlled_substance")
    private Boolean isControlledSubstance;

    @Size(max = 5)
    @Column(name = "dea_schedule", length = 5)
    private String deaSchedule;

    @ColumnDefault("false")
    @Column(name = "is_generic")
    private Boolean isGeneric;

    @Size(max = 100)
    @Column(name = "therapeutic_class", length = 100)
    private String therapeuticClass;

    @Size(max = 100)
    @Column(name = "pharmacologic_class", length = 100)
    private String pharmacologicClass;

    @ColumnDefault("true")
    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "discontinuation_date")
    private LocalDate discontinuationDate;
}