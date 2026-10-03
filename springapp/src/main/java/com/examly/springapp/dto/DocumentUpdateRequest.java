package com.examly.springapp.dto;

import jakarta.validation.constraints.Size;

public class DocumentUpdateRequest {

    @Size(min = 1, max = 255, message = "Document name must not be empty")
    private String name;

    private Long parentFolderId;

    private String tags;

    public DocumentUpdateRequest() {
    }

    public DocumentUpdateRequest(String name, Long parentFolderId, String tags) {
        this.name = name;
        this.parentFolderId = parentFolderId;
        this.tags = tags;
    }

    public static DocumentUpdateRequestBuilder builder() {
        return new DocumentUpdateRequestBuilder();
    }

    public static class DocumentUpdateRequestBuilder {
        private String name;
        private Long parentFolderId;
        private String tags;

        public DocumentUpdateRequestBuilder name(String name) { this.name = name; return this; }
        public DocumentUpdateRequestBuilder parentFolderId(Long parentFolderId) { this.parentFolderId = parentFolderId; return this; }
        public DocumentUpdateRequestBuilder tags(String tags) { this.tags = tags; return this; }

        public DocumentUpdateRequest build() {
            return new DocumentUpdateRequest(name, parentFolderId, tags);
        }
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Long getParentFolderId() { return parentFolderId; }
    public void setParentFolderId(Long parentFolderId) { this.parentFolderId = parentFolderId; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
}
