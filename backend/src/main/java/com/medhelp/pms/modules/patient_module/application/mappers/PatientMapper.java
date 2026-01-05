package com.medhelp.pms.modules.patient_module.application.mappers;

import com.medhelp.pms.modules.patient_module.domain.application.dtos.PatientRequest;
import com.medhelp.pms.modules.patient_module.domain.application.dtos.PatientResponse;
import com.medhelp.pms.modules.patient_module.domain.entities.Patient;
import org.springframework.stereotype.Component;

@Component
public class PatientMapper {

    public Patient toEntity(PatientRequest request) {
        if (request == null) {
            return null;
        }

        Patient patient = new Patient();
        updateEntity(patient, request);
        return patient;
    }

    public void updateEntity(Patient patient, PatientRequest request) {
        if (request == null || patient == null) {
            return;
        }

        patient.setFirstName(request.getFirstName());
        patient.setMiddleName(request.getMiddleName());
        patient.setLastName(request.getLastName());
        patient.setDateOfBirth(request.getDateOfBirth());
        patient.setGender(request.getGender());
        patient.setSsnLastFour(request.getSsnLastFour());
        patient.setPhonePrimary(request.getPhone());
        patient.setPhoneSecondary(request.getPhoneSecondary());
        patient.setEmail(request.getEmail());

        if (request.getAddress() != null) {
            patient.setAddressLine1(request.getAddress().getLine1());
            patient.setAddressLine2(request.getAddress().getLine2());
            patient.setCity(request.getAddress().getCity());
            patient.setState(request.getAddress().getState());
            patient.setZipCode(request.getAddress().getZipCode());
        }
        patient.setCountry(request.getCountry() != null ? request.getCountry() : "US");
        patient.setLanguagePreference(request.getLanguagePreference() != null ? request.getLanguagePreference() : "en");
        patient.setCommunicationPreference(
                request.getCommunicationPreference() != null ? request.getCommunicationPreference() : "sms");
        patient.setAllowGenericSubstitution(
                request.getAllowGenericSubstitution() != null ? request.getAllowGenericSubstitution() : true);
    }

    public PatientResponse toResponse(Patient patient) {
        if (patient == null) {
            return null;
        }

        return PatientResponse.builder()
                .id(patient.getId())
                .patientNumber(patient.getPatientNumber())
                .firstName(patient.getFirstName())
                .middleName(patient.getMiddleName())
                .lastName(patient.getLastName())
                .dateOfBirth(patient.getDateOfBirth())
                .gender(patient.getGender())
                .ssnLastFour(patient.getSsnLastFour())
                .phonePrimary(patient.getPhonePrimary())
                .phoneSecondary(patient.getPhoneSecondary())
                .email(patient.getEmail())
                .addressLine1(patient.getAddressLine1())
                .addressLine2(patient.getAddressLine2())
                .city(patient.getCity())
                .state(patient.getState())
                .zipCode(patient.getZipCode())
                .country(patient.getCountry())
                .languagePreference(patient.getLanguagePreference())
                .communicationPreference(patient.getCommunicationPreference())
                .allowGenericSubstitution(patient.getAllowGenericSubstitution())
                .isActive(patient.getIsActive())
                .createdAt(patient.getCreatedAt())
                .updatedAt(patient.getUpdatedAt())
                .build();
    }
}
