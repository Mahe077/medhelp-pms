package com.medhelp.pms.modules.patient_module.domain.application.usecases;

import com.medhelp.pms.modules.patient_module.domain.repositories.PatientRepository;
import com.medhelp.pms.shared.domain.events.DomainEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdatePatientUseCase {
    private final PatientRepository patientRepository;
    private DomainEventPublisher domainEventPublisher;
}
