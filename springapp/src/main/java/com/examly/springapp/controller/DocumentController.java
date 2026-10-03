package com.examly.springapp.controller;

import com.examly.springapp.dto.DocumentResponse;
import com.examly.springapp.dto.DocumentUpdateRequest;
import com.examly.springapp.service.ArchiveService;
import com.examly.springapp.service.DocumentService;
import com.examly.springapp.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
@Tag(name = "Documents", description = "Document management, upload, download, and search APIs")
@SecurityRequirement(name = "BearerAuth")
public class DocumentController {

    private final DocumentService documentService;
    private final ArchiveService archiveService;

    public DocumentController(DocumentService documentService, ArchiveService archiveService) {
        this.documentService = documentService;
        this.archiveService = archiveService;
    }

    @GetMapping
    @Operation(summary = "Get or search documents", description = "Returns active documents belonging to the authenticated user, with optional search and filtering")
    public ResponseEntity<List<DocumentResponse>> getDocuments(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) Long minSize,
            @RequestParam(required = false) Long maxSize) {

        if (name != null || type != null || tag != null || minSize != null || maxSize != null) {
            return ResponseEntity.ok(documentService.searchDocuments(name, type, tag, minSize, maxSize));
        }
        return ResponseEntity.ok(documentService.getMyDocuments());
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload document", description = "Upload a document file (up to 50MB) with optional parent folder and tags")
    public ResponseEntity<DocumentResponse> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "parentFolderId", required = false) Long parentFolderId,
            @RequestParam(value = "tags", required = false) String tags) {

        DocumentResponse response = documentService.uploadDocument(file, parentFolderId, tags);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get document metadata", description = "Returns metadata for a specific document owned by the user or viewed by admin")
    public ResponseEntity<DocumentResponse> getDocumentById(@PathVariable Long id) {
        return ResponseEntity.ok(documentService.getDocumentById(id));
    }

    @GetMapping("/{id}/download")
    @Operation(summary = "Download document file", description = "Streams the document file content for download")
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long id) {
        Resource resource = documentService.downloadDocument(id);
        String filename = documentService.getDocumentFilename(id);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(resource);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update document metadata", description = "Updates document name, parent folder, or tags")
    public ResponseEntity<DocumentResponse> updateDocument(
            @PathVariable Long id,
            @Valid @RequestBody DocumentUpdateRequest request) {
        return ResponseEntity.ok(documentService.updateDocument(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete / archive document", description = "Soft-deletes a document and moves it to archive")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        documentService.deleteDocument(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/archived")
    @Operation(summary = "Get archived documents", description = "Returns list of archived documents for the authenticated user")
    public ResponseEntity<List<DocumentResponse>> getArchivedDocuments() {
        String email = SecurityUtil.getCurrentUserEmail();
        return ResponseEntity.ok(archiveService.getArchivedDocuments(email));
    }

    @PutMapping("/{id}/restore")
    @Operation(summary = "Restore archived document", description = "Restores an archived document back to the active list")
    public ResponseEntity<DocumentResponse> restoreDocument(@PathVariable Long id) {
        String email = SecurityUtil.getCurrentUserEmail();
        return ResponseEntity.ok(archiveService.restoreDocument(email, id));
    }
}
