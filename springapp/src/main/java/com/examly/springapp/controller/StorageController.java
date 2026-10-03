package com.examly.springapp.controller;

import com.examly.springapp.dto.StorageUsageResponse;
import com.examly.springapp.service.QuotaService;
import com.examly.springapp.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/storage")
@Tag(name = "Storage & Quota", description = "User storage usage and quota metrics")
@SecurityRequirement(name = "BearerAuth")
public class StorageController {

    private final QuotaService quotaService;

    public StorageController(QuotaService quotaService) {
        this.quotaService = quotaService;
    }

    @GetMapping("/usage")
    @Operation(summary = "Get user storage usage", description = "Returns storage used, quota limit, and percentage consumed")
    public ResponseEntity<StorageUsageResponse> getStorageUsage() {
        String email = SecurityUtil.getCurrentUserEmail();
        return ResponseEntity.ok(quotaService.getStorageUsage(email));
    }
}
