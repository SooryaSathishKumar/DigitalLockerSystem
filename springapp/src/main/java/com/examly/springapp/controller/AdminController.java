package com.examly.springapp.controller;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.examly.springapp.dto.ActivityLogResponse;
import com.examly.springapp.dto.AdminUserRequest;
import com.examly.springapp.dto.DocumentResponse;
import com.examly.springapp.dto.UserResponse;
import com.examly.springapp.service.AdminService;
import com.examly.springapp.service.StorageSettings;
import com.examly.springapp.util.SecurityUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin", description = "Administrative operations for users, logs, and system quotas")
@SecurityRequirement(name = "BearerAuth")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/users")
    @Operation(summary = "Get all users (paginated)", description = "Retrieves a paginated list of users in the system")
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(adminService.getAllUsers(pageable));
    }

    @GetMapping("/users/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieves details of a specific user")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getUserById(id));
    }

    @PostMapping("/users")
    @Operation(summary = "Create user", description = "Creates a new user with specific role")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody AdminUserRequest request) {
        UserResponse response = adminService.createUser(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/users/{id}")
    @Operation(summary = "Update user", description = "Updates user information")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody AdminUserRequest request) {
        return ResponseEntity.ok(adminService.updateUser(id, request, SecurityUtil.getCurrentUserEmail()));
    }

    @DeleteMapping("/users/{id}")
    @Operation(summary = "Delete user", description = "Deletes a user. Admin cannot delete their own account.")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        String adminEmail = SecurityUtil.getCurrentUserEmail();
        adminService.deleteUser(id, adminEmail);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/users/{id}/reset-password")
    @Operation(summary = "Reset user password", description = "Resets the password of a user")
    public ResponseEntity<Void> resetPassword(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        String newPassword = request.get("newPassword");
        adminService.resetPassword(id, newPassword);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/documents")
    @Operation(summary = "Get all documents", description = "Returns all documents uploaded across the system")
    public ResponseEntity<List<DocumentResponse>> getAllDocuments() {
        return ResponseEntity.ok(adminService.getAllDocuments());
    }

    @GetMapping("/activity-logs")
    @Operation(summary = "Get system activity logs", description = "Retrieves paginated system-wide activity logs")
    public ResponseEntity<Page<ActivityLogResponse>> getActivityLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").descending());
        return ResponseEntity.ok(adminService.getSystemActivityLogs(pageable));
    }

    @PutMapping("/storage/quota")
    @Operation(summary = "Configure default storage quota", description = "Configures system-wide default storage quota per user in bytes")
    public ResponseEntity<Void> configureQuota(@RequestBody Map<String, Long> payload) {
        Long quota = payload.get("quota");
        if (quota == null) {
            throw new IllegalArgumentException("Quota field is required");
        }
        adminService.configureQuota(quota);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/storage/settings")
    @Operation(summary = "Get storage settings", description = "Returns the current system storage quota settings")
    public ResponseEntity<StorageSettings> getStorageSettings() {
        return ResponseEntity.ok(adminService.getStorageSettings());
    }

    @PutMapping("/storage/max-file-size")
    @Operation(summary = "Configure max upload file size", description = "Configures maximum allowed single file upload size in bytes")
    public ResponseEntity<Void> configureMaxFileSize(@RequestBody Map<String, Long> payload) {
        Long maxFileSize = payload.get("maxFileSize");
        if (maxFileSize == null) {
            throw new IllegalArgumentException("maxFileSize field is required");
        }
        adminService.configureMaxFileSize(maxFileSize);
        return ResponseEntity.ok().build();
    }
}
