package com.medhelp.pms.shared.infrastructure.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MockEmailService implements EmailService {

    @Override
    public void sendVerificationEmail(String toInfo, String verificationLink) {
        log.info("=================================================");
        log.info("MOCK EMAIL SENDING Service");
        log.info("To: {}", toInfo);
        log.info("Subject: Verify your email");
        log.info("Body: Please click the link to verify your email: {}", verificationLink);
        log.info("This link will expire in 24 hours.");
        log.info("=================================================");
    }
}
