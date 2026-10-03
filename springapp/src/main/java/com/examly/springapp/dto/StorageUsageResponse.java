package com.examly.springapp.dto;

public class StorageUsageResponse {

    private Long storageUsed;
    private Long storageQuota;
    private Double usagePercentage;
    private Long maxFileSize;

    public StorageUsageResponse() {
    }

    public StorageUsageResponse(Long storageUsed, Long storageQuota, Double usagePercentage, Long maxFileSize) {
        this.storageUsed = storageUsed;
        this.storageQuota = storageQuota;
        this.usagePercentage = usagePercentage;
        this.maxFileSize = maxFileSize;
    }

    public static StorageUsageResponseBuilder builder() {
        return new StorageUsageResponseBuilder();
    }

    public static class StorageUsageResponseBuilder {
        private Long storageUsed;
        private Long storageQuota;
        private Double usagePercentage;
        private Long maxFileSize;

        public StorageUsageResponseBuilder storageUsed(Long storageUsed) { this.storageUsed = storageUsed; return this; }
        public StorageUsageResponseBuilder storageQuota(Long storageQuota) { this.storageQuota = storageQuota; return this; }
        public StorageUsageResponseBuilder usagePercentage(Double usagePercentage) { this.usagePercentage = usagePercentage; return this; }
        public StorageUsageResponseBuilder maxFileSize(Long maxFileSize) { this.maxFileSize = maxFileSize; return this; }

        public StorageUsageResponse build() {
            return new StorageUsageResponse(storageUsed, storageQuota, usagePercentage, maxFileSize);
        }
    }

    public Long getStorageUsed() { return storageUsed; }
    public void setStorageUsed(Long storageUsed) { this.storageUsed = storageUsed; }

    public Long getStorageQuota() { return storageQuota; }
    public void setStorageQuota(Long storageQuota) { this.storageQuota = storageQuota; }

    public Double getUsagePercentage() { return usagePercentage; }
    public void setUsagePercentage(Double usagePercentage) { this.usagePercentage = usagePercentage; }

    public Long getMaxFileSize() { return maxFileSize; }
    public void setMaxFileSize(Long maxFileSize) { this.maxFileSize = maxFileSize; }
}
