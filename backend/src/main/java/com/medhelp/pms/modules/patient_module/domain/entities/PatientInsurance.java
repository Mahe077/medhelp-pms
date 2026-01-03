package com.medhelp.pms.modules.patient_module.domain.entities;

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
@Table(name = "patient_insurance", schema = "patient_schema")
public class PatientInsurance {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @Size(max = 255)
    @NotNull
    @Column(name = "insurance_provider", nullable = false)
    private String insuranceProvider;

    @Size(max = 50)
    @Column(name = "insurance_type", length = 50)
    private String insuranceType;

    @Size(max = 100)
    @NotNull
    @Column(name = "policy_number", nullable = false, length = 100)
    private String policyNumber;

    @Size(max = 100)
    @Column(name = "group_number", length = 100)
    private String groupNumber;

    @Size(max = 20)
    @Column(name = "bin_number", length = 20)
    private String binNumber;

    @Size(max = 20)
    @Column(name = "pcn_number", length = 20)
    private String pcnNumber;

    @Size(max = 255)
    @Column(name = "cardholder_name")
    private String cardholderName;

    @Size(max = 50)
    @Column(name = "relationship_to_cardholder", length = 50)
    private String relationshipToCardholder;

    @NotNull
    @Column(name = "effective_date", nullable = false)
    private LocalDate effectiveDate;

    @Column(name = "expiration_date")
    private LocalDate expirationDate;

    @ColumnDefault("true")
    @Column(name = "is_active")
    private Boolean isActive;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}