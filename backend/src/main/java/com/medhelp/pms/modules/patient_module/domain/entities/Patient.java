package com.medhelp.pms.modules.patient_module.domain.entities;

import com.medhelp.pms.modules.auth_module.domain.entities.User;
import com.medhelp.pms.shared.domain.BaseEntity;
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
@Table(name = "patients", schema = "patient_schema")
public class Patient extends BaseEntity {
    @Size(max = 20)
    @NotNull
    @Column(name = "patient_number", nullable = false, length = 20)
    private String patientNumber;

    @Size(max = 100)
    @NotNull
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Size(max = 100)
    @Column(name = "middle_name", length = 100)
    private String middleName;

    @Size(max = 100)
    @NotNull
    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @NotNull
    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Size(max = 20)
    @Column(name = "gender", length = 20)
    private String gender;

    @Size(max = 4)
    @Column(name = "ssn_last_four", length = 4)
    private String ssnLastFour;

    @Size(max = 20)
    @NotNull
    @Column(name = "phone_primary", nullable = false, length = 20)
    private String phonePrimary;

    @Size(max = 20)
    @Column(name = "phone_secondary", length = 20)
    private String phoneSecondary;

    @Size(max = 255)
    @Column(name = "email")
    private String email;

    @Size(max = 255)
    @NotNull
    @Column(name = "address_line1", nullable = false)
    private String addressLine1;

    @Size(max = 255)
    @Column(name = "address_line2")
    private String addressLine2;

    @Size(max = 100)
    @NotNull
    @Column(name = "city", nullable = false, length = 100)
    private String city;

    @Size(max = 50)
    @NotNull
    @Column(name = "state", nullable = false, length = 50)
    private String state;

    @Size(max = 10)
    @NotNull
    @Column(name = "zip_code", nullable = false, length = 10)
    private String zipCode;

    @Size(max = 2)
    @ColumnDefault("'US'")
    @Column(name = "country", length = 2)
    private String country;

    @Size(max = 10)
    @ColumnDefault("'en'")
    @Column(name = "language_preference", length = 10)
    private String languagePreference;

    @Size(max = 20)
    @ColumnDefault("'sms'")
    @Column(name = "communication_preference", length = 20)
    private String communicationPreference;

    @ColumnDefault("true")
    @Column(name = "allow_generic_substitution")
    private Boolean allowGenericSubstitution;

    @ColumnDefault("true")
    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}