package com.medhelp.pms.modules.patient_module.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "patient_prescribers", schema = "patient_schema")
public class PatientPrescriber {
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
    @Column(name = "prescriber_name", nullable = false)
    private String prescriberName;

    @Size(max = 10)
    @NotNull
    @Column(name = "prescriber_npi", nullable = false, length = 10)
    private String prescriberNpi;

    @Size(max = 20)
    @Column(name = "prescriber_dea", length = 20)
    private String prescriberDea;

    @Size(max = 100)
    @Column(name = "specialty", length = 100)
    private String specialty;

    @Size(max = 20)
    @Column(name = "phone", length = 20)
    private String phone;

    @ColumnDefault("false")
    @Column(name = "is_primary")
    private Boolean isPrimary;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private LocalDateTime createdAt;

}