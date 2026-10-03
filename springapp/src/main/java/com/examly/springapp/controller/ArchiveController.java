package com.examly.springapp.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.examly.springapp.dto.DocumentResponse;
import com.examly.springapp.service.ArchiveService;
import com.examly.springapp.util.SecurityUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/archive")
@Tag(name = "Archive", description = "Document archiving, retention, and restoration APIs")
@SecurityRequirement(name = "BearerAuth")
public class ArchiveController {

    private final ArchiveService archiveService;

    public ArchiveController(ArchiveService archiveService) {
        this.archiveService = archiveService;
    }

    @GetMapping("/documents")
    @Operation(summary = "Get archived documents", description = "Retrieves all archived documents for the authenticated user")
    public ResponseEntity<List<DocumentResponse>> getArchivedDocuments() {
        String email = SecurityUtil.getCurrentUserEmail();
        return ResponseEntity.ok(archiveService.getArchivedDocuments(email));
    }

    @PutMapping("/documents/{id}/restore")
    @Operation(summary = "Restore archived document", description = "Restores an archived document back to the active list")
    public ResponseEntity<DocumentResponse> restoreDocument(@PathVariable Long id) {
        String email = SecurityUtil.getCurrentUserEmail();
        return ResponseEntity.ok(archiveService.restoreDocument(email, id));
    }

    @DeleteMapping("/documents/{id}/permanent")
    @Operation(summary = "Permanently purge document", description = "Permanently deletes an archived document from storage and database")
    public ResponseEntity<Void> permanentlyDelete(@PathVariable Long id) {
        archiveService.permanentlyDelete(SecurityUtil.getCurrentUserEmail(), id);
        return ResponseEntity.noContent().build();
    }
}
