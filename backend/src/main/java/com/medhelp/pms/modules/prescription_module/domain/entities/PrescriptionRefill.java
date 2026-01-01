package com.medhelp.pms.modules.prescription_module.domain.entities;

import com.medhelp.pms.modules.billing_module.domain.entities.User;
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
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "prescription_refills", schema = "prescription_schema")
public class PrescriptionRefill extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "prescription_id")
    private Prescription prescription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prescription_item_id")
    private PrescriptionItem prescriptionItem;

    @NotNull
    @Column(name = "refill_number", nullable = false)
    private Integer refillNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medication_id")
    private Medication medication;

    @Size(max = 11)
    @NotNull
    @Column(name = "ndc_code", nullable = false, length = 11)
    private String ndcCode;

    @NotNull
    @Column(name = "quantity_dispensed", nullable = false, precision = 10, scale = 2)
    private BigDecimal quantityDispensed;

    @NotNull
    @Column(name = "days_supply", nullable = false)
    private Integer daysSupply;

    @NotNull
    @Column(name = "filled_date", nullable = false)
    private LocalDate filledDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "filled_by")
    private User filledBy;

    @Column(name = "billed_amount", precision = 10, scale = 2)
    private BigDecimal billedAmount;

}