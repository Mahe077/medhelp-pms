package com.medhelp.pms.modules.patient_module.domain.application.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientRequest {
    @NotBlank(message = "First name is required")
    @Size(max = 100)
    private String firstName;

    @Size(max = 100)
    private String middleName;

    @NotBlank(message = "Last name is required")
    @Size(max = 100)
    private String lastName;

    @NotNull(message = "Date of birth is required")
    private LocalDate dateOfBirth;

    @Size(max = 20)
    private String gender;

    @Size(max = 4)
    private String ssnLastFour;

    @NotBlank(message = "Phone number is required")
    @Size(max = 20)
    private String phone; // FE sends "phone"

    @Size(max = 20)
    private String phoneSecondary;

    @Email(message = "Invalid email format")
    @Size(max = 255)
    private String email;

    @Valid
    @NotNull(message = "Address details are required")
    private AddressRequest address; // FE sends "address" as nested object

    @Size(max = 2)
    private String country;

    @Size(max = 10)
    private String languagePreference;

    @Size(max = 20)
    private String communicationPreference;

    private Boolean allowGenericSubstitution;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddressRequest {
        @NotBlank(message = "Address line 1 is required")
        @Size(max = 255)
        private String line1;

        @Size(max = 255)
        private String line2;

        @NotBlank(message = "City is required")
        @Size(max = 100)
        private String city;

        @NotBlank(message = "State is required")
        @Size(max = 50)
        private String state;

        @NotBlank(message = "Zip code is required")
        @Size(max = 10)
        private String zipCode;
    }
}
