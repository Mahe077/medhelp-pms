package com.medhelp.pms.modules.patient_module.api.controllers;

import com.medhelp.pms.modules.patient_module.domain.application.dtos.PatientRequest;
import com.medhelp.pms.modules.patient_module.domain.application.dtos.PatientResponse;
import com.medhelp.pms.modules.patient_module.domain.services.PatientService;
import com.medhelp.pms.shared.api.validators.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/patients")
@RequiredArgsConstructor
@Tag(name = "Patient Management", description = "Endpoints for managing patients")
public class PatientController {
    private final PatientService patientService;

    @GetMapping
    @Operation(summary = "Get all patients", description = "Retrieves a list of all patients")
    public ResponseEntity<ApiResponse<List<PatientResponse>>> getAllPatients() {
        return ResponseEntity.ok(ApiResponse.success(patientService.getPatient()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get patient by ID", description = "Retrieves a single patient by their unique identifier")
    public ResponseEntity<ApiResponse<PatientResponse>> getPatientById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(patientService.getPatientById(id)));
    }

    @PostMapping
    @Operation(summary = "Create new patient", description = "Creates a new patient record")
    public ResponseEntity<ApiResponse<PatientResponse>> createPatient(@Valid @RequestBody PatientRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(patientService.createPatient(request)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update patient", description = "Updates an existing patient record")
    public ResponseEntity<ApiResponse<PatientResponse>> updatePatient(
            @PathVariable UUID id,
            @Valid @RequestBody PatientRequest request) {
        return ResponseEntity.ok(ApiResponse.success(patientService.updatePatient(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete patient", description = "Deletes a patient record")
    public ResponseEntity<ApiResponse<Void>> deletePatient(@PathVariable UUID id) {
        patientService.deletePatient(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
