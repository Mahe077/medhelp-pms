package com.medhelp.pms.modules.patient_module.domain.services;

import com.medhelp.pms.modules.patient_module.application.mappers.PatientMapper;
import com.medhelp.pms.modules.patient_module.domain.application.dtos.PatientRequest;
import com.medhelp.pms.modules.patient_module.domain.application.dtos.PatientResponse;
import com.medhelp.pms.modules.patient_module.domain.entities.Patient;
import com.medhelp.pms.modules.patient_module.domain.repositories.PatientRepository;
import com.medhelp.pms.shared.domain.exceptions.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PatientService {
    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;

    public List<PatientResponse> getPatient() {
        return patientRepository.findAll().stream()
                .map(patientMapper::toResponse)
                .toList();
    }

    public PatientResponse getPatientById(UUID id) {
        return patientRepository.findById(id)
                .map(patientMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Patient not found with id: " + id));
    }

    @Transactional
    public PatientResponse createPatient(PatientRequest request) {
        log.info("Creating new patient: {} {}", request.getFirstName(), request.getLastName());
        Patient patient = patientMapper.toEntity(request);
        patient.setPatientNumber(generatePatientNumber());
        patient.setIsActive(true);

        Patient savedPatient = patientRepository.save(patient);
        return patientMapper.toResponse(savedPatient);
    }

    @Transactional
    public PatientResponse updatePatient(UUID id, PatientRequest request) {
        log.info("Updating patient with id: {}", id);
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Patient not found with id: " + id));

        patientMapper.updateEntity(patient, request);
        Patient savedPatient = patientRepository.save(patient);
        return patientMapper.toResponse(savedPatient);
    }

    @Transactional
    public void deletePatient(UUID id) {
        log.info("Deleting patient with id: {}", id);
        if (!patientRepository.existsById(id)) {
            throw new EntityNotFoundException("Patient not found with id: " + id);
        }
        patientRepository.deleteById(id);
    }

    private String generatePatientNumber() {
        // Simple generation logic: PAT-YYYYMMDD-XXXX
        return "PAT-" + System.currentTimeMillis() % 1000000;
    }
}
