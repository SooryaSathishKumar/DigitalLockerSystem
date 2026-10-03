package com.examly.springapp.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.examly.springapp.dto.ActivityLogResponse;
import com.examly.springapp.dto.AdminUserRequest;
import com.examly.springapp.dto.DocumentResponse;
import com.examly.springapp.dto.UserResponse;
import com.examly.springapp.exception.DuplicateEmailException;
import com.examly.springapp.exception.UnauthorizedAccessException;
import com.examly.springapp.exception.UserNotFoundException;
import com.examly.springapp.model.Document;
import com.examly.springapp.model.Role;
import com.examly.springapp.model.User;
import com.examly.springapp.repository.DocumentRepository;
import com.examly.springapp.repository.UserRepository;

@Service
@Transactional
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final DocumentRepository documentRepository;
    private final ActivityLogService activityLogService;
    private final QuotaService quotaService;
    private final PasswordEncoder passwordEncoder;

    public AdminServiceImpl(UserRepository userRepository,
                            DocumentRepository documentRepository,
                            ActivityLogService activityLogService,
                            QuotaService quotaService,
                            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.documentRepository = documentRepository;
        this.activityLogService = activityLogService;
        this.quotaService = quotaService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(this::mapToUserResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        return mapToUserResponse(user);
    }

    @Override
    public UserResponse createUser(AdminUserRequest request) {
        if (request.getPassword() == null || request.getPassword().trim().length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long");
        }
        String email = request.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateEmailException("Email " + email + " is already in use");
        }

        User user = User.builder()
                .name(request.getName().trim())
                .email(email)
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole() != null ? request.getRole() : Role.USER)
                .storageUsed(0L)
                .createdAt(LocalDateTime.now())
                .build();

        User saved = userRepository.save(user);
        return mapToUserResponse(saved);
    }

    @Override
    public UserResponse updateUser(Long id, AdminUserRequest request, String adminEmail) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        if (user.getEmail().equalsIgnoreCase(adminEmail)
                && request.getRole() != null && request.getRole() != Role.ADMIN) {
            throw new UnauthorizedAccessException("Administrators cannot remove their own administrator role");
        }

        String newEmail = request.getEmail().trim().toLowerCase();
        if (!user.getEmail().equalsIgnoreCase(newEmail)) {
            if (userRepository.existsByEmail(newEmail)) {
                throw new DuplicateEmailException("Email " + newEmail + " is already in use");
            }
            user.setEmail(newEmail);
        }

        user.setName(request.getName().trim());

        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }

        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }

        User updated = userRepository.save(user);
        return mapToUserResponse(updated);
    }

    @Override
    public void deleteUser(Long id, String adminEmail) {
        User userToDelete = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        // Critical SRS rule: An ADMIN must NOT be allowed to delete their own account
        if (userToDelete.getEmail().equalsIgnoreCase(adminEmail)) {
            throw new UnauthorizedAccessException("Administrators are strictly prohibited from deleting their own account");
        }

        userRepository.delete(userToDelete);
    }

    @Override
    public void resetPassword(Long id, String newPassword) {
        if (newPassword == null || newPassword.trim().length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentResponse> getAllDocuments() {
        return documentRepository.findAll().stream()
                .map(this::mapToDocResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ActivityLogResponse> getSystemActivityLogs(Pageable pageable) {
        return activityLogService.getAllActivityLogs(pageable);
    }

    @Override
    public void configureQuota(long quota) {
        if (quota <= 0) {
            throw new IllegalArgumentException("Storage quota must be greater than zero");
        }
        quotaService.setDefaultQuota(quota);
    }

    @Override
    public void configureMaxFileSize(long maxFileSize) {
        if (maxFileSize <= 0) {
            throw new IllegalArgumentException("Max file size must be greater than zero");
        }
        quotaService.setMaxFileSize(maxFileSize);
    }

    @Override
    @Transactional(readOnly = true)
    public StorageSettings getStorageSettings() {
        return new StorageSettings(quotaService.getDefaultQuota(), quotaService.getMaxFileSize());
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .storageUsed(user.getStorageUsed() != null ? user.getStorageUsed() : 0L)
                .createdAt(user.getCreatedAt())
                .build();
    }

    private DocumentResponse mapToDocResponse(Document doc) {
        return DocumentResponse.builder()
                .id(doc.getId())
                .name(doc.getName())
                .fileType(doc.getFileType())
                .size(doc.getSize())
                .uploadedAt(doc.getUploadedAt())
                .parentFolderId(doc.getParentFolder() != null ? doc.getParentFolder().getId() : null)
                .isArchived(doc.getIsArchived())
                .tags(doc.getTags())
                .ownerId(doc.getOwner() != null ? doc.getOwner().getId() : null)
                .ownerName(doc.getOwner() != null ? doc.getOwner().getName() : null)
                .build();
    }
}
