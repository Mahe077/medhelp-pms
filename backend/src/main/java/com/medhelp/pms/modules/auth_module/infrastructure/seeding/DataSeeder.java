package com.medhelp.pms.modules.auth_module.infrastructure.seeding;

import com.medhelp.pms.modules.auth_module.domain.entities.User;
import com.medhelp.pms.modules.auth_module.domain.repositories.AuthRepository;
import com.medhelp.pms.modules.auth_module.domain.value_objects.SystemUser;
import com.medhelp.pms.modules.auth_module.domain.value_objects.UserType;
import com.medhelp.pms.modules.auth_module.domain.services.AccessControlService;
import com.medhelp.pms.modules.auth_module.domain.entities.Role;
import com.medhelp.pms.modules.auth_module.domain.entities.Permission;
import com.medhelp.pms.modules.auth_module.domain.repositories.PermissionRepository;
import com.medhelp.pms.modules.auth_module.domain.repositories.RoleRepository;
import java.util.List;
import java.util.Set;

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
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final AccessControlService accessControlService;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (roleRepository.count() == 0) {
            log.info("Seeding Roles and Permissions...");
            seedRolesAndPermissions();
        }

        if (authRepository.count() == 0) {
            log.info("Database is empty, seeding initial data...");
            seedAdminUser();
        }

        seedSystemUser(); // always make sure the system user exists
    }

    private void seedRolesAndPermissions() {
        // 1. Create Permissions
        String[] resources = {
                "USER", "ROLE", "PERMISSION", "SETTINGS", "AUDIT_LOG",
                "PATIENT", "DOCTOR", "MEDICATION", "PRESCRIPTION", "INVENTORY"
        };
        String[] actions = { "CREATE", "READ", "UPDATE", "DELETE" };

        for (String resource : resources) {
            for (String action : actions) {
                String name = resource + "_" + action;
                String description = "Allow " + action + " on " + resource;
                if (!permissionRepository.existsByName(name)) {
                    accessControlService.createPermission(name, description, resource, action);
                }
            }
        }

        // 2. Create Roles
        createRoleIfNotExists("ADMIN", "System Administrator - Full Access");
        createRoleIfNotExists("DOCTOR", "Medical Doctor - Clinical Access");
        createRoleIfNotExists("PHARMACIST", "Pharmacist - Medication & Inventory Access");
        createRoleIfNotExists("RECEPTIONIST", "Receptionist - Front Desk & Patient Management");
        createRoleIfNotExists("PATIENT", "Patient - Personal Health Record Access");

        // 3. Assign Permissions to Roles
        assignAllPermissionsToRole("ADMIN");

        // Doctor Permissions
        assignPermissionsToRole("DOCTOR", List.of(
                "PATIENT_READ", "PATIENT_UPDATE",
                "PRESCRIPTION_CREATE", "PRESCRIPTION_READ", "PRESCRIPTION_UPDATE",
                "MEDICATION_READ"));

        // Pharmacist Permissions
        assignPermissionsToRole("PHARMACIST", List.of(
                "PRESCRIPTION_READ", "PRESCRIPTION_UPDATE",
                "MEDICATION_CREATE", "MEDICATION_READ", "MEDICATION_UPDATE", "MEDICATION_DELETE",
                "INVENTORY_CREATE", "INVENTORY_READ", "INVENTORY_UPDATE", "INVENTORY_DELETE"));

        // Receptionist Permissions
        assignPermissionsToRole("RECEPTIONIST", List.of(
                "PATIENT_CREATE", "PATIENT_READ", "PATIENT_UPDATE",
                "DOCTOR_READ"));
    }

    private void createRoleIfNotExists(String name, String description) {
        if (!roleRepository.existsByName(name)) {
            accessControlService.createRole(name, description);
        }
    }

    private void assignAllPermissionsToRole(String roleName) {
        Role role = roleRepository.findByName(roleName).orElseThrow();
        List<Permission> allPermissions = permissionRepository.findAll();
        for (Permission permission : allPermissions) {
            try {
                accessControlService.assignPermissionToRole(role.getId(), permission.getId());
            } catch (Exception e) {
                // Ignore if already assigned
                log.error("Something went wrong while assigning all permissions to role {}.", roleName, e);
            }
        }
    }

    private void assignPermissionsToRole(String roleName, List<String> permissionNames) {
        Role role = roleRepository.findByName(roleName).orElseThrow();
        for (String permName : permissionNames) {
            permissionRepository.findByName(permName).ifPresent(permission -> {
                try {
                    accessControlService.assignPermissionToRole(role.getId(), permission.getId());
                } catch (Exception e) {
                    // Ignore if already assigned
                    log.error("Something went wrong while assigning permissions to role {}.", roleName, e);
                }
            });
        }
    }

    private void seedSystemUser() {
        if (authRepository.existsByUsername(SystemUser.USERNAME)) {
            log.info("System user is already in use");
            return;
        }

        Role adminRole = roleRepository.findByName("ADMIN").orElseThrow();

        User systemUser = User.builder()
                .username(SystemUser.USERNAME)
                .email("system@medhelp.com")
                .passwordHash("") // No password for system user
                .firstName("System")
                .lastName("User")
                .roles(Set.of(adminRole))
                .userType(UserType.INTERNAL) // you can add SYSTEM type in your enum
                .isActive(true)
                .isEmailVerified(true)
                .preferredLanguage("en")
                .preferredTheme("system")
                .build();

        authRepository.save(systemUser);
        log.info("Default System user created with UUID {}", systemUser.getId());
    }

    private void seedAdminUser() {
        Role adminRole = roleRepository.findByName("ADMIN").orElseThrow();

        User admin = User.builder()
                .username("admin")
                .email("admin@medhelp.com")
                .passwordHash(passwordEncoder.encode("admin123"))
                .firstName("System")
                .lastName("Administrator")
                .roles(Set.of(adminRole))
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
