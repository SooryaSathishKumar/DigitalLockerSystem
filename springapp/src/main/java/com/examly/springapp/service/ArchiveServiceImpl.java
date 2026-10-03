package com.examly.springapp.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.examly.springapp.dto.DocumentResponse;
import com.examly.springapp.exception.DocumentNotFoundException;
import com.examly.springapp.exception.UnauthorizedAccessException;
import com.examly.springapp.exception.UserNotFoundException;
import com.examly.springapp.model.Document;
import com.examly.springapp.model.Role;
import com.examly.springapp.model.User;
import com.examly.springapp.repository.DocumentRepository;
import com.examly.springapp.repository.UserRepository;

@Service
@Transactional
public class ArchiveServiceImpl implements ArchiveService {

    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;
    private final ActivityLogService activityLogService;
    private final StorageService storageService;
    private final QuotaService quotaService;

    public ArchiveServiceImpl(DocumentRepository documentRepository, UserRepository userRepository, ActivityLogService activityLogService, StorageService storageService, QuotaService quotaService) {
        this.documentRepository = documentRepository;
        this.userRepository = userRepository;
        this.activityLogService = activityLogService;
        this.storageService = storageService;
        this.quotaService = quotaService;
    }

    @Override
    public void archiveDocument(String userEmail, Long documentId) {
        User user = getUser(userEmail);
        Document document = getDocument(documentId);

        if (!document.getOwner().getId().equals(user.getId())) {
            throw new UnauthorizedAccessException("You are not authorized to archive this document");
        }

        document.setIsArchived(true);
        documentRepository.save(document);

        activityLogService.log(user, document, "ARCHIVE");
    }

    @Override
    public DocumentResponse restoreDocument(String userEmail, Long documentId) {
        User user = getUser(userEmail);
        Document document = getDocument(documentId);

        if (!document.getOwner().getId().equals(user.getId())) {
            throw new UnauthorizedAccessException("You are not authorized to restore this document");
        }

        document.setIsArchived(false);
        Document saved = documentRepository.save(document);

        activityLogService.log(user, saved, "RESTORE");
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentResponse> getArchivedDocuments(String userEmail) {
        User user = getUser(userEmail);
        return documentRepository.findByOwnerAndIsArchivedTrue(user).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void permanentlyDelete(String userEmail, Long documentId) {
        User user = getUser(userEmail);
        Document document = getDocument(documentId);

        if (!document.getOwner().getId().equals(user.getId()) && user.getRole() != Role.ADMIN) {
            throw new UnauthorizedAccessException("You are not authorized to permanently delete this document");
        }

        User owner = document.getOwner();

        storageService.delete(document.getFileUrl());
        quotaService.reduceStorageUsage(owner, document.getSize());
        activityLogService.log(owner, document, "DELETE");
        documentRepository.delete(document);
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
    }

    private Document getDocument(Long id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new DocumentNotFoundException("Document with id " + id + " not found"));
    }

    private DocumentResponse mapToResponse(Document document) {
        return DocumentResponse.builder()
                .id(document.getId())
                .name(document.getName())
                .fileType(document.getFileType())
                .size(document.getSize())
                .uploadedAt(document.getUploadedAt())
                .parentFolderId(document.getParentFolder() != null ? document.getParentFolder().getId() : null)
                .isArchived(document.getIsArchived())
                .tags(document.getTags())
                .ownerId(document.getOwner() != null ? document.getOwner().getId() : null)
                .ownerName(document.getOwner() != null ? document.getOwner().getName() : null)
                .build();
    }
}
