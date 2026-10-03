package com.examly.springapp.service;

import com.examly.springapp.dto.StorageUsageResponse;
import com.examly.springapp.exception.StorageQuotaExceededException;
import com.examly.springapp.exception.UserNotFoundException;
import com.examly.springapp.model.User;
import com.examly.springapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class QuotaServiceImpl implements QuotaService {

    private final UserRepository userRepository;

    @Value("${app.storage.default-quota:524288000}")
    private long defaultQuota = 524288000L; // 500 MB

    @Value("${app.storage.max-file-size:52428800}")
    private long maxFileSize = 52428800L; // 50 MB

    public QuotaServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void checkQuota(User user, long newFileSize) {
        long currentUsed = user.getStorageUsed() != null ? user.getStorageUsed() : 0L;
        if (currentUsed + newFileSize > defaultQuota) {
            throw new StorageQuotaExceededException("Storage quota of " + (defaultQuota / (1024 * 1024)) + " MB exceeded. Current usage: " + currentUsed + " bytes, required: " + newFileSize + " bytes");
        }
    }

    @Override
    public void addStorageUsage(User user, long fileSize) {
        long currentUsed = user.getStorageUsed() != null ? user.getStorageUsed() : 0L;
        user.setStorageUsed(currentUsed + fileSize);
        userRepository.save(user);
    }

    @Override
    public void reduceStorageUsage(User user, long fileSize) {
        long currentUsed = user.getStorageUsed() != null ? user.getStorageUsed() : 0L;
        long updated = Math.max(0L, currentUsed - fileSize);
        user.setStorageUsed(updated);
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public StorageUsageResponse getStorageUsage(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + userEmail));

        long used = user.getStorageUsed() != null ? user.getStorageUsed() : 0L;
        double percentage = defaultQuota > 0 ? ((double) used / defaultQuota) * 100.0 : 0.0;

        return StorageUsageResponse.builder()
                .storageUsed(used)
                .storageQuota(defaultQuota)
                .usagePercentage(Math.round(percentage * 100.0) / 100.0)
                .maxFileSize(maxFileSize)
                .build();
    }

    @Override
    public long getDefaultQuota() {
        return defaultQuota;
    }

    @Override
    public void setDefaultQuota(long quota) {
        this.defaultQuota = quota;
    }

    @Override
    public long getMaxFileSize() {
        return maxFileSize;
    }

    @Override
    public void setMaxFileSize(long maxFileSize) {
        this.maxFileSize = maxFileSize;
    }
}
