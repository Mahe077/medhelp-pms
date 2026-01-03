package com.medhelp.pms.modules.billing_module.domain.entities;

import com.medhelp.pms.modules.auth_module.domain.entities.User;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "insurance_claims", schema = "billing_schema")
public class InsuranceClaim {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Size(max = 50)
    @NotNull
    @Column(name = "claim_number", nullable = false, length = 50)
    private String claimNumber;

    @NotNull
    @Column(name = "prescription_id", nullable = false)
    private UUID prescriptionId;

    @NotNull
    @Column(name = "patient_insurance_id", nullable = false)
    private UUID patientInsuranceId;

    @NotNull
    @Column(name = "submission_date", nullable = false)
    private LocalDate submissionDate;

    @NotNull
    @Column(name = "submitted_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal submittedAmount;

    @Column(name = "approved_amount", precision = 10, scale = 2)
    private BigDecimal approvedAmount;

    @Column(name = "patient_responsibility", precision = 10, scale = 2)
    private BigDecimal patientResponsibility;

    @Size(max = 50)
    @NotNull
    @ColumnDefault("'submitted'")
    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Size(max = 20)
    @Column(name = "rejection_code", length = 20)
    private String rejectionCode;

    @Column(name = "rejection_reason", length = Integer.MAX_VALUE)
    private String rejectionReason;

    @Column(name = "response_date")
    private LocalDate responseDate;

    @Column(name = "payment_date")
    private LocalDate paymentDate;

    @Size(max = 100)
    @Column(name = "external_claim_id", length = 100)
    private String externalClaimId;

    @Column(name = "notes", length = Integer.MAX_VALUE)
    private String notes;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}