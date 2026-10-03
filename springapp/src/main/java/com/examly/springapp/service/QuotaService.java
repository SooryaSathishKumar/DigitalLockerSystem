package com.examly.springapp.service;

import com.examly.springapp.dto.StorageUsageResponse;
import com.examly.springapp.model.User;

public interface QuotaService {

    void checkQuota(User user, long newFileSize);

    void addStorageUsage(User user, long fileSize);

    void reduceStorageUsage(User user, long fileSize);

    StorageUsageResponse getStorageUsage(String userEmail);

    long getDefaultQuota();

    void setDefaultQuota(long quota);

    long getMaxFileSize();

    void setMaxFileSize(long maxFileSize);
}
