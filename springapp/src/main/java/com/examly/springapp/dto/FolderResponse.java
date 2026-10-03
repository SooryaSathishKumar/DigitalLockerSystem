package com.examly.springapp.dto;

import java.time.LocalDateTime;

public class FolderResponse {

    private Long id;
    private String name;
    private Long parentFolderId;
    private LocalDateTime createdAt;

    public FolderResponse() {
    }

    public FolderResponse(Long id, String name, Long parentFolderId, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.parentFolderId = parentFolderId;
        this.createdAt = createdAt;
    }

    public static FolderResponseBuilder builder() {
        return new FolderResponseBuilder();
    }

    public static class FolderResponseBuilder {
        private Long id;
        private String name;
        private Long parentFolderId;
        private LocalDateTime createdAt;

        public FolderResponseBuilder id(Long id) { this.id = id; return this; }
        public FolderResponseBuilder name(String name) { this.name = name; return this; }
        public FolderResponseBuilder parentFolderId(Long parentFolderId) { this.parentFolderId = parentFolderId; return this; }
        public FolderResponseBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public FolderResponse build() {
            return new FolderResponse(id, name, parentFolderId, createdAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Long getParentFolderId() { return parentFolderId; }
    public void setParentFolderId(Long parentFolderId) { this.parentFolderId = parentFolderId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
