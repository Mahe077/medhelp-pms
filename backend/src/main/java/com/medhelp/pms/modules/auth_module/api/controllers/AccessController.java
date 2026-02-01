package com.medhelp.pms.modules.auth_module.api.controllers;

import com.medhelp.pms.shared.application.dtos.AuditLogDto;
import com.medhelp.pms.modules.auth_module.domain.services.AccessControlService;
import com.medhelp.pms.shared.api.validators.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/access")
@RequiredArgsConstructor
public class AccessController {

    private final AccessControlService accessControlService;

    @GetMapping("/audit-logs")
    public ResponseEntity<ApiResponse<List<AuditLogDto>>> getAuditLogs() {
        return ResponseEntity.ok(ApiResponse.success(accessControlService.getAuditLogs()));
    }
}
