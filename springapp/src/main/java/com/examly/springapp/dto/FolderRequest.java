package com.examly.springapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class FolderRequest {

    @NotBlank(message = "Folder name is required")
    @Size(min = 1, max = 255, message = "Folder name must be between 1 and 255 characters")
    private String name;

    private Long parentFolderId;

    public FolderRequest() {
    }

    public FolderRequest(String name, Long parentFolderId) {
        this.name = name;
        this.parentFolderId = parentFolderId;
    }

    public static FolderRequestBuilder builder() {
        return new FolderRequestBuilder();
    }

    public static class FolderRequestBuilder {
        private String name;
        private Long parentFolderId;

        public FolderRequestBuilder name(String name) { this.name = name; return this; }
        public FolderRequestBuilder parentFolderId(Long parentFolderId) { this.parentFolderId = parentFolderId; return this; }

        public FolderRequest build() {
            return new FolderRequest(name, parentFolderId);
        }
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Long getParentFolderId() { return parentFolderId; }
    public void setParentFolderId(Long parentFolderId) { this.parentFolderId = parentFolderId; }
}
