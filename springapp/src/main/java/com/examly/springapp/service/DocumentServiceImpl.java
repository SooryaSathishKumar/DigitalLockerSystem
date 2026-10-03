package com.examly.springapp.service;

import com.examly.springapp.dto.DocumentResponse;
import com.examly.springapp.dto.DocumentUpdateRequest;
import com.examly.springapp.exception.DocumentNotFoundException;
import com.examly.springapp.exception.FolderNotFoundException;
import com.examly.springapp.exception.UnauthorizedAccessException;
import com.examly.springapp.exception.UserNotFoundException;
import com.examly.springapp.model.Document;
import com.examly.springapp.model.Folder;
import com.examly.springapp.model.Role;
import com.examly.springapp.model.User;
import com.examly.springapp.repository.DocumentRepository;
import com.examly.springapp.repository.FolderRepository;
import com.examly.springapp.repository.UserRepository;
import com.examly.springapp.util.FileValidationUtil;
import com.examly.springapp.util.SecurityUtil;
import jakarta.persistence.criteria.Predicate;
import org.springframework.core.io.Resource;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;
    private final FolderRepository folderRepository;
    private final StorageService storageService;
    private final QuotaService quotaService;
    private final ActivityLogService activityLogService;

    public DocumentServiceImpl(DocumentRepository documentRepository,
                               UserRepository userRepository,
                               FolderRepository folderRepository,
                               StorageService storageService,
                               QuotaService quotaService,
                               ActivityLogService activityLogService) {
        this.documentRepository = documentRepository;
        this.userRepository = userRepository;
        this.folderRepository = folderRepository;
        this.storageService = storageService;
        this.quotaService = quotaService;
        this.activityLogService = activityLogService;
    }

    @Override
    public DocumentResponse uploadDocument(MultipartFile file) {
        return uploadDocument(file, null, null);
    }

    @Override
    public DocumentResponse uploadDocument(MultipartFile file, Long parentFolderId, String tags) {
        User user = getCurrentUser();

        // 1. Validate file format and size (SRS: max 50MB)
        FileValidationUtil.validateFile(file, quotaService.getMaxFileSize());

        // 2. Check storage quota (SRS: default 500MB)
        quotaService.checkQuota(user, file.getSize());

        // 3. Resolve parent folder if supplied
        Folder parentFolder = null;
        if (parentFolderId != null) {
            parentFolder = folderRepository.findById(parentFolderId)
                    .orElseThrow(() -> new FolderNotFoundException("Parent folder with id " + parentFolderId + " not found"));
            if (!parentFolder.getOwner().getId().equals(user.getId())) {
                throw new UnauthorizedAccessException("Cannot upload file into another user's folder");
            }
        }

        // 4. Store file using StorageService
        String fileUrl = storageService.store(file, user.getId());

        // 5. Create Document entity
        String originalFilename = file.getOriginalFilename();
        String fileExtension = FileValidationUtil.getFileExtension(originalFilename).toUpperCase();

        Document document = Document.builder()
                .owner(user)
                .name(originalFilename != null ? originalFilename : "untitled")
                .fileType(fileExtension)
                .fileUrl(fileUrl)
                .size(file.getSize())
                .parentFolder(parentFolder)
                .isArchived(false)
                .tags(tags)
                .uploadedAt(LocalDateTime.now())
                .build();

        Document savedDoc = documentRepository.save(document);

        // 6. Update user's storageUsed
        quotaService.addStorageUsage(user, file.getSize());

        // 7. Create ActivityLog
        activityLogService.log(user, savedDoc, "UPLOAD");

        // 8. Return response DTO
        return mapToResponse(savedDoc);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentResponse> getMyDocuments() {
        User user = getCurrentUser();
        return documentRepository.findByOwnerAndIsArchivedFalse(user).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentResponse> searchDocuments(String name, String fileType, String tag, Long minSize, Long maxSize) {
        User user = getCurrentUser();

        Specification<Document> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("owner"), user));
            predicates.add(cb.equal(root.get("isArchived"), false));

            if (name != null && !name.trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.trim().toLowerCase() + "%"));
            }
            if (fileType != null && !fileType.trim().isEmpty()) {
                predicates.add(cb.equal(cb.upper(root.get("fileType")), fileType.trim().toUpperCase()));
            }
            if (tag != null && !tag.trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("tags")), "%" + tag.trim().toLowerCase() + "%"));
            }
            if (minSize != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("size"), minSize));
            }
            if (maxSize != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("size"), maxSize));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return documentRepository.findAll(spec).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentResponse getDocumentById(Long id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new DocumentNotFoundException("Document with id " + id + " not found"));

        User currentUser = getCurrentUser();
        boolean isOwner = document.getOwner().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;

        if (!isOwner && !isAdmin) {
            throw new UnauthorizedAccessException("You are not authorized to view this document");
        }

        return mapToResponse(document);
    }

    @Override
    public Resource downloadDocument(Long id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new DocumentNotFoundException("Document with id " + id + " not found"));

        User currentUser = getCurrentUser();
        boolean isOwner = document.getOwner().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;

        if (!isOwner && !isAdmin) {
            throw new UnauthorizedAccessException("You are not authorized to download this document");
        }

        Resource resource = storageService.download(document.getFileUrl());
        activityLogService.log(currentUser, document, "DOWNLOAD");
        return resource;
    }

    @Override
    public DocumentResponse updateDocument(Long id, DocumentUpdateRequest request) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new DocumentNotFoundException("Document with id " + id + " not found"));

        User currentUser = getCurrentUser();
        if (!document.getOwner().getId().equals(currentUser.getId())) {
            throw new UnauthorizedAccessException("You are not authorized to update this document");
        }

        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            document.setName(request.getName().trim());
        }

        if (request.getTags() != null) {
            document.setTags(request.getTags().trim());
        }

        if (request.getParentFolderId() == null) {
            document.setParentFolder(null);
        } else {
            Folder newFolder = folderRepository.findById(request.getParentFolderId())
                    .orElseThrow(() -> new FolderNotFoundException("Parent folder with id " + request.getParentFolderId() + " not found"));
            if (!newFolder.getOwner().getId().equals(currentUser.getId())) {
                throw new UnauthorizedAccessException("Cannot move document to another user's folder");
            }
            document.setParentFolder(newFolder);
        }

        Document updated = documentRepository.save(document);
        activityLogService.log(currentUser, updated, "RENAME");
        return mapToResponse(updated);
    }

    @Override
    public void deleteDocument(Long id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new DocumentNotFoundException("Document with id " + id + " not found"));

        User currentUser = getCurrentUser();
        if (!document.getOwner().getId().equals(currentUser.getId())) {
            throw new UnauthorizedAccessException("You are not authorized to delete this document");
        }

        document.setIsArchived(true);
        documentRepository.save(document);

        activityLogService.log(currentUser, document, "DELETE");
    }

    @Override
    @Transactional(readOnly = true)
    public String getDocumentFilename(Long id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new DocumentNotFoundException("Document with id " + id + " not found"));
        return document.getName();
    }

    private User getCurrentUser() {
        String email = SecurityUtil.getCurrentUserEmail();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
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
