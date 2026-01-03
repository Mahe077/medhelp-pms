package com.medhelp.pms.modules.auth_module.infrastructure.seeding;

import com.medhelp.pms.modules.auth_module.domain.entities.User;
import com.medhelp.pms.modules.auth_module.domain.repositories.AuthRepository;
import com.medhelp.pms.modules.auth_module.domain.value_objects.UserType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (authRepository.count() == 0) {
            log.info("Database is empty, seeding initial data...");
            seedAdminUser();
        }
    }

    private void seedAdminUser() {
        User admin = User.builder()
                .username("admin")
                .email("admin@medhelp.com")
                .passwordHash(passwordEncoder.encode("admin123"))
                .firstName("System")
                .lastName("Administrator")
                .role("ADMIN")
                .userType(UserType.INTERNAL)
                .isActive(true)
                .isEmailVerified(true)
                .preferredLanguage("en")
                .preferredTheme("system")
                .build();

        authRepository.save(admin);
        log.info("Default Admin user created: admin / admin123");
    }
}
