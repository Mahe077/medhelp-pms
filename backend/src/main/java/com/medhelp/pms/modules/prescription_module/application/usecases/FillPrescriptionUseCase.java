package com.medhelp.pms.modules.prescription_module.application.usecases;

import com.medhelp.pms.modules.prescription_module.domain.repositories.PrescriptionRepository;
import com.medhelp.pms.shared.domain.events.DomainEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class FillPrescriptionUseCase {
    private final PrescriptionRepository prescriptionRepository;
    private final DomainEventPublisher eventPublisher;


}
