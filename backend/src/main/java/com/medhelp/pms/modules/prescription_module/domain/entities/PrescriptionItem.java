package com.medhelp.pms.modules.prescription_module.domain.entities;

import com.medhelp.pms.shared.domain.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "prescription_items", schema = "prescription_schema")
public class PrescriptionItem extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "prescription_id")
    private Prescription prescription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medication_id")
    private Medication medication;

    @Size(max = 255)
    @NotNull
    @Column(name = "written_medication_name", nullable = false)
    private String writtenMedicationName;

    @NotNull
    @Column(name = "quantity", nullable = false, precision = 10, scale = 2)
    private BigDecimal quantity;

    @Size(max = 20)
    @NotNull
    @Column(name = "quantity_unit", nullable = false, length = 20)
    private String quantityUnit;

    @NotNull
    @Column(name = "days_supply", nullable = false)
    private Integer daysSupply;

    @NotNull
    @Column(name = "sig", nullable = false, length = Integer.MAX_VALUE)
    private String sig;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "refills_authorized", nullable = false)
    private Integer refillsAuthorized;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "refills_remaining", nullable = false)
    private Integer refillsRemaining;

    @ColumnDefault("true")
    @Column(name = "substitution_allowed")
    private Boolean substitutionAllowed;

    @Size(max = 2)
    @Column(name = "daw_code", length = 2)
    private String dawCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dispensed_medication_id")
    private Medication dispensedMedication;

    @Column(name = "dispensed_quantity", precision = 10, scale = 2)
    private BigDecimal dispensedQuantity;

    @Size(max = 11)
    @Column(name = "dispensed_ndc", length = 11)
    private String dispensedNdc;
}