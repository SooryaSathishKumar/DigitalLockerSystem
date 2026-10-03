package com.examly.springapp.controller;

import com.examly.springapp.dto.FolderRequest;
import com.examly.springapp.dto.FolderResponse;
import com.examly.springapp.service.FolderService;
import com.examly.springapp.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/folders")
@Tag(name = "Folders", description = "Folder hierarchy management APIs")
@SecurityRequirement(name = "BearerAuth")
public class FolderController {

    private final FolderService folderService;

    public FolderController(FolderService folderService) {
        this.folderService = folderService;
    }

    @GetMapping
    @Operation(summary = "Get user folders", description = "Returns all folders created by the authenticated user")
    public ResponseEntity<List<FolderResponse>> getFolders(
            @RequestParam(required = false) Long parentFolderId) {
        String email = SecurityUtil.getCurrentUserEmail();
        if (parentFolderId != null) {
            return ResponseEntity.ok(folderService.getChildFolders(email, parentFolderId));
        }
        return ResponseEntity.ok(folderService.getFolders(email));
    }

    @PostMapping
    @Operation(summary = "Create folder", description = "Creates a new folder or subfolder")
    public ResponseEntity<FolderResponse> createFolder(@Valid @RequestBody FolderRequest request) {
        String email = SecurityUtil.getCurrentUserEmail();
        FolderResponse response = folderService.createFolder(email, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update folder", description = "Renames or moves an existing folder")
    public ResponseEntity<FolderResponse> updateFolder(
            @PathVariable Long id,
            @Valid @RequestBody FolderRequest request) {
        String email = SecurityUtil.getCurrentUserEmail();
        FolderResponse response = folderService.renameFolder(email, id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete folder", description = "Deletes a folder and all its contents")
    public ResponseEntity<Void> deleteFolder(@PathVariable Long id) {
        String email = SecurityUtil.getCurrentUserEmail();
        folderService.deleteFolder(email, id);
        return ResponseEntity.noContent().build();
    }
}
