package com.medhelp.pms.modules.prescription_module.domain.entities;

import com.medhelp.pms.modules.auth_module.domain.entities.User;
import com.medhelp.pms.modules.prescription_module.domain.value_objects.PrescriptionStatus;
import com.medhelp.pms.shared.domain.BaseEntity;
import com.medhelp.pms.shared.domain.exceptions.BusinessException;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "prescriptions", schema = "prescription_schema")
public class Prescription extends BaseEntity {
    @Size(max = 20)
    @NotNull
    @Column(name = "prescription_number", nullable = false, length = 20)
    private String prescriptionNumber;

    @NotNull
    @Column(name = "patient_id", nullable = false)
    private UUID patientId;

    @Size(max = 255)
    @NotNull
    @Column(name = "prescriber_name", nullable = false)
    private String prescriberName;

    @Size(max = 10)
    @NotNull
    @Column(name = "prescriber_npi", nullable = false, length = 10)
    private String prescriberNpi;

    @Size(max = 20)
    @Column(name = "prescriber_dea", length = 20)
    private String prescriberDea;

    @Size(max = 20)
    @Column(name = "prescriber_phone", length = 20)
    private String prescriberPhone;

    @Column(name = "prescriber_address", length = Integer.MAX_VALUE)
    private String prescriberAddress;

    @NotNull
    @Column(name = "prescription_date", nullable = false)
    private LocalDate prescriptionDate;

    @NotNull
    @Column(name = "written_date", nullable = false)
    private LocalDate writtenDate;

    @NotNull
    @Column(name = "expiration_date", nullable = false)
    private LocalDate expirationDate;

    @Size(max = 20)
    @NotNull
    @Column(name = "source", nullable = false, length = 20)
    private String source;

    @Size(max = 50)
    @Column(name = "external_rx_number", length = 50)
    private String externalRxNumber;

    @Enumerated(EnumType.STRING)
    @ColumnDefault("'received'")
    @Column(name = "status", nullable = false, length = 50)
    private PrescriptionStatus status;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "received_at")
    private LocalDateTime receivedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "received_by")
    private User receivedBy;

    @Column(name = "validated_at")
    private LocalDateTime validatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "validated_by")
    private User validatedBy;

    @Column(name = "filled_at")
    private LocalDateTime filledAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "filled_by")
    private User filledBy;

    @Column(name = "dispensed_at")
    private LocalDateTime dispensedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dispensed_by")
    private User dispensedBy;

    @ColumnDefault("false")
    @Column(name = "is_stat")
    private Boolean isStat;

    @ColumnDefault("true")
    @Column(name = "requires_counseling")
    private Boolean requiresCounseling;

    @ColumnDefault("false")
    @Column(name = "counseling_completed")
    private Boolean counselingCompleted;

    @ColumnDefault("false")
    @Column(name = "counseling_declined")
    private Boolean counselingDeclined;

    @Column(name = "notes", length = Integer.MAX_VALUE)
    private String notes;

    @Column(name = "rejection_reason", length = Integer.MAX_VALUE)
    private String rejectionReason;

    // Business methods
    public void validate() {
        if (status != PrescriptionStatus.RECEIVED) {
            throw new BusinessException("Cannot validate prescription in status: " + status);
        }
        this.status = PrescriptionStatus.VALIDATED;
    }

    public void fill() {
        if (status != PrescriptionStatus.VALIDATED) {
            throw new BusinessException("Cannot fill unvalidated prescription");
        }
        this.status = PrescriptionStatus.FILLED;
    }
}