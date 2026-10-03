package com.examly.springapp.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String fileType;

    @Column(nullable = false)
    private String fileUrl;

    @Column(nullable = false)
    private Long size;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_folder_id")
    private Folder parentFolder;

    @Column(nullable = false)
    private Boolean isArchived = false;

    private String tags;

    @Column(nullable = false)
    private LocalDateTime uploadedAt;

    public Document() {
    }

    public Document(Long id, User owner, String name, String fileType, String fileUrl, Long size, Folder parentFolder, Boolean isArchived, String tags, LocalDateTime uploadedAt) {
        this.id = id;
        this.owner = owner;
        this.name = name;
        this.fileType = fileType;
        this.fileUrl = fileUrl;
        this.size = size;
        this.parentFolder = parentFolder;
        this.isArchived = isArchived != null ? isArchived : false;
        this.tags = tags;
        this.uploadedAt = uploadedAt;
    }

    @PrePersist
    protected void onCreate() {
        if (uploadedAt == null) {
            uploadedAt = LocalDateTime.now();
        }
        if (isArchived == null) {
            isArchived = false;
        }
    }

    public static DocumentBuilder builder() {
        return new DocumentBuilder();
    }

    public static class DocumentBuilder {
        private Long id;
        private User owner;
        private String name;
        private String fileType;
        private String fileUrl;
        private Long size;
        private Folder parentFolder;
        private Boolean isArchived = false;
        private String tags;
        private LocalDateTime uploadedAt;

        public DocumentBuilder id(Long id) { this.id = id; return this; }
        public DocumentBuilder owner(User owner) { this.owner = owner; return this; }
        public DocumentBuilder name(String name) { this.name = name; return this; }
        public DocumentBuilder fileType(String fileType) { this.fileType = fileType; return this; }
        public DocumentBuilder fileUrl(String fileUrl) { this.fileUrl = fileUrl; return this; }
        public DocumentBuilder size(Long size) { this.size = size; return this; }
        public DocumentBuilder parentFolder(Folder parentFolder) { this.parentFolder = parentFolder; return this; }
        public DocumentBuilder isArchived(Boolean isArchived) { this.isArchived = isArchived; return this; }
        public DocumentBuilder tags(String tags) { this.tags = tags; return this; }
        public DocumentBuilder uploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; return this; }

        public Document build() {
            return new Document(id, owner, name, fileType, fileUrl, size, parentFolder, isArchived, tags, uploadedAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }

    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }

    public Long getSize() { return size; }
    public void setSize(Long size) { this.size = size; }

    public Folder getParentFolder() { return parentFolder; }
    public void setParentFolder(Folder parentFolder) { this.parentFolder = parentFolder; }

    public Boolean getIsArchived() { return isArchived; }
    public void setIsArchived(Boolean isArchived) { this.isArchived = isArchived; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
}
