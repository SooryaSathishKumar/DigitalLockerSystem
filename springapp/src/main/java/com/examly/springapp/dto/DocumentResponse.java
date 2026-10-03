package com.examly.springapp.dto;

import java.time.LocalDateTime;

public class DocumentResponse {

    private Long id;
    private String name;
    private String fileType;
    private Long size;
    private LocalDateTime uploadedAt;
    private Long parentFolderId;
    private Boolean isArchived;
    private String tags;
    private Long ownerId;
    private String ownerName;

    public DocumentResponse() {
    }

    public DocumentResponse(Long id, String name, String fileType, Long size, LocalDateTime uploadedAt, Long parentFolderId, Boolean isArchived, String tags, Long ownerId, String ownerName) {
        this.id = id;
        this.name = name;
        this.fileType = fileType;
        this.size = size;
        this.uploadedAt = uploadedAt;
        this.parentFolderId = parentFolderId;
        this.isArchived = isArchived;
        this.tags = tags;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
    }

    public static DocumentResponseBuilder builder() {
        return new DocumentResponseBuilder();
    }

    public static class DocumentResponseBuilder {
        private Long id;
        private String name;
        private String fileType;
        private Long size;
        private LocalDateTime uploadedAt;
        private Long parentFolderId;
        private Boolean isArchived;
        private String tags;
        private Long ownerId;
        private String ownerName;

        public DocumentResponseBuilder id(Long id) { this.id = id; return this; }
        public DocumentResponseBuilder name(String name) { this.name = name; return this; }
        public DocumentResponseBuilder fileType(String fileType) { this.fileType = fileType; return this; }
        public DocumentResponseBuilder size(Long size) { this.size = size; return this; }
        public DocumentResponseBuilder uploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; return this; }
        public DocumentResponseBuilder parentFolderId(Long parentFolderId) { this.parentFolderId = parentFolderId; return this; }
        public DocumentResponseBuilder isArchived(Boolean isArchived) { this.isArchived = isArchived; return this; }
        public DocumentResponseBuilder tags(String tags) { this.tags = tags; return this; }
        public DocumentResponseBuilder ownerId(Long ownerId) { this.ownerId = ownerId; return this; }
        public DocumentResponseBuilder ownerName(String ownerName) { this.ownerName = ownerName; return this; }

        public DocumentResponse build() {
            return new DocumentResponse(id, name, fileType, size, uploadedAt, parentFolderId, isArchived, tags, ownerId, ownerName);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }

    public Long getSize() { return size; }
    public void setSize(Long size) { this.size = size; }

    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }

    public Long getParentFolderId() { return parentFolderId; }
    public void setParentFolderId(Long parentFolderId) { this.parentFolderId = parentFolderId; }

    public Boolean getIsArchived() { return isArchived; }
    public void setIsArchived(Boolean isArchived) { this.isArchived = isArchived; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
}
