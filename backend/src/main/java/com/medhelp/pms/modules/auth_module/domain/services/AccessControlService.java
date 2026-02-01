package com.medhelp.pms.modules.auth_module.domain.services;

import com.medhelp.pms.shared.application.dtos.AuditLogDto;
import com.medhelp.pms.modules.auth_module.domain.entities.*;
import com.medhelp.pms.modules.auth_module.domain.repositories.PermissionRepository;
import com.medhelp.pms.modules.auth_module.domain.repositories.RolePermissionRepository;
import com.medhelp.pms.modules.auth_module.domain.repositories.RoleRepository;
import com.medhelp.pms.shared.domain.events.DomainEvent;
import com.medhelp.pms.shared.domain.events.DomainEventPublisher;
import com.medhelp.pms.shared.domain.events.entities.repositories.DomainEventRepository;
import com.medhelp.pms.shared.domain.exceptions.BusinessException;
import com.medhelp.pms.shared.infrastructure.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccessControlService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final DomainEventPublisher eventPublisher;
    private final DomainEventRepository domainEventRepository;
    private final AuthenticationService authenticationService;

    public static class AccessAuditEvent extends DomainEvent {
        private final Map<String, Object> data;

        public AccessAuditEvent(String eventType, String aggregateType, String aggregateId, String userId,
                Map<String, Object> data) {
            super(eventType, "1.0", aggregateType, aggregateId, userId);
            this.data = data;
        }

        @Override
        public Object getData() {
            return data;
        }
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public List<Permission> getAllPermissions() {
        return permissionRepository.findAll();
    }

    @Transactional
    public Role createRole(String name, String description) {
        if (roleRepository.existsByName(name)) {
            throw new BusinessException("Role with name " + name + " already exists");
        }
        Role role = new Role();
        role.setName(name);
        role.setDescription(description);
        role.setCreatedAt(LocalDateTime.now());
        Role savedRole = roleRepository.save(role);

        // Audit
        publishAuditEvent("ROLE_CREATED", "ROLE", savedRole.getId(), "Role created: " + name);

        return savedRole;
    }

    @Transactional
    public Permission createPermission(String name, String description, String resource, String action) {
        if (permissionRepository.existsByName(name)) {
            throw new BusinessException("Permission with name " + name + " already exists");
        }
        Permission permission = new Permission();
        permission.setName(name);
        permission.setDescription(description);
        permission.setResource(resource);
        permission.setAction(action);
        permission.setCreatedAt(LocalDateTime.now());
        Permission savedPermission = permissionRepository.save(permission);

        // Audit
        publishAuditEvent("PERMISSION_CREATED", "PERMISSION", savedPermission.getId(), "Permission created: " + name);

        return savedPermission;
    }

    public List<Permission> getPermissionsForRole(UUID roleId) {
        List<RolePermission> rolePermissions = rolePermissionRepository.findAllByRoleId(roleId);
        return rolePermissions.stream()
                .map(RolePermission::getPermission)
                .collect(Collectors.toList());
    }

    @Transactional
    public void assignPermissionToRole(UUID roleId, UUID permissionId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new BusinessException("Role not found"));
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new BusinessException("Permission not found"));

        RolePermissionId id = new RolePermissionId();
        id.setRoleId(roleId);
        id.setPermissionId(permissionId);

        if (rolePermissionRepository.existsById(id)) {
            return;
        }

        RolePermission rolePermission = new RolePermission();
        rolePermission.setId(id);
        rolePermission.setRole(role);
        rolePermission.setPermission(permission);
        rolePermission.setCreatedAt(LocalDateTime.now());

        rolePermissionRepository.save(rolePermission);

        // Audit
        publishAuditEvent("PERMISSION_ASSIGNED", "ROLE", roleId,
                "Permission " + permission.getName() + " assigned to role " + role.getName());
    }

    @Transactional
    public void removePermissionFromRole(UUID roleId, UUID permissionId) {
        RolePermissionId id = new RolePermissionId();
        id.setRoleId(roleId);
        id.setPermissionId(permissionId);

        rolePermissionRepository.deleteById(id);

        // Audit
        publishAuditEvent("PERMISSION_REMOVED", "ROLE", roleId, "Permission removed from role");
    }

    @Transactional(readOnly = true)
    public List<AuditLogDto> getAuditLogs() {
        return domainEventRepository.findAllByOrderByOccurredAtDesc(PageRequest.of(0, 100))
                .getContent().stream()
                .map(event -> AuditLogDto.builder()
                        .id(event.getEventId())
                        .eventType(event.getEventType())
                        .description(extractDescription(event.getEventData()))
                        .aggregateType(event.getAggregateType())
                        .aggregateId(event.getAggregateId())
                        .occurredAt(event.getOccurredAt())
                        .userId(event.getUserId())
                        .userName(event.getUserId() != null ? event.getUserId().toString() : "System")
                        .eventData(event.getEventData() != null ? event.getEventData().toString() : "{}")
                        .sequenceNumber(event.getSequenceNumber())
                        .build())
                .collect(Collectors.toList());
    }

    private String extractDescription(Map<String, Object> eventData) {
        if (eventData != null && eventData.containsKey("description")) {
            return String.valueOf(eventData.get("description"));
        }
        return "No description";
    }

    private void publishAuditEvent(String eventType, String aggregateType, UUID aggregateId, String description) {
        try {
            java.util.Map<String, Object> data = new java.util.HashMap<>();
            data.put("description", description);

            String userId = authenticationService.getSystemUserId().toString();
            try {
                User currentUser = SecurityUtils.getCurrentUser();
                if (currentUser != null) {
                    userId = currentUser.getId().toString();
                }
            } catch (Exception e) {
                // Ignore if no user context
                log.error(e.getMessage(), e);
            }

            AccessAuditEvent event = new AccessAuditEvent(
                    eventType,
                    aggregateType,
                    aggregateId.toString(),
                    userId,
                    data);
            eventPublisher.publish(event);
        } catch (Exception e) {
            log.error("Failed to publish audit event", e);
        }
    }
}
