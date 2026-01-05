package com.medhelp.pms.modules.patient_module.domain.repositories;

import com.medhelp.pms.modules.patient_module.domain.entities.Patient;
import com.medhelp.pms.modules.prescription_module.domain.value_objects.PrescriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PatientRepository extends JpaRepository<Patient, UUID> {
}
