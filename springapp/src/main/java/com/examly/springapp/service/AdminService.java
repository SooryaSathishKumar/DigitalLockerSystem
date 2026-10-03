package com.examly.springapp.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.examly.springapp.dto.ActivityLogResponse;
import com.examly.springapp.dto.AdminUserRequest;
import com.examly.springapp.dto.DocumentResponse;
import com.examly.springapp.dto.UserResponse;

public interface AdminService {

    Page<UserResponse> getAllUsers(Pageable pageable);

    UserResponse getUserById(Long id);

    UserResponse createUser(AdminUserRequest request);

    UserResponse updateUser(Long id, AdminUserRequest request, String adminEmail);

    void deleteUser(Long id, String adminEmail);

    void resetPassword(Long id, String newPassword);

    List<DocumentResponse> getAllDocuments();

    Page<ActivityLogResponse> getSystemActivityLogs(Pageable pageable);

    void configureQuota(long quota);

    void configureMaxFileSize(long maxFileSize);

    StorageSettings getStorageSettings();
}
