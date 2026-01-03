package com.medhelp.pms.shared.infrastructure.notification;

public interface EmailService {
    void sendVerificationEmail(String toInfo, String verificationLink);
}
