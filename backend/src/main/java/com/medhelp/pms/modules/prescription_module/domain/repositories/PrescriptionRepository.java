package com.medhelp.pms.modules.prescription_module.domain.repositories;

import com.medhelp.pms.modules.prescription_module.domain.entities.Prescription;
import com.medhelp.pms.modules.prescription_module.domain.value_objects.PrescriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PrescriptionRepository extends JpaRepository<Prescription, UUID> {
    Optional<Prescription> findByPrescriptionNumber(String prescriptionNumber);
    List<Prescription> findByPatientId(UUID patientId);
    List<Prescription> findByStatus(PrescriptionStatus status);
}
