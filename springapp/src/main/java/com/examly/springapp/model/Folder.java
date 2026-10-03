package com.examly.springapp.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "folders")
public class Folder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_folder_id")
    private Folder parentFolder;

    @OneToMany(mappedBy = "parentFolder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Folder> subFolders = new ArrayList<>();

    @OneToMany(mappedBy = "parentFolder", cascade = CascadeType.ALL)
    private List<Document> documents = new ArrayList<>();

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public Folder() {
    }

    public Folder(Long id, User owner, String name, Folder parentFolder, LocalDateTime createdAt) {
        this.id = id;
        this.owner = owner;
        this.name = name;
        this.parentFolder = parentFolder;
        this.createdAt = createdAt;
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public static FolderBuilder builder() {
        return new FolderBuilder();
    }

    public static class FolderBuilder {
        private Long id;
        private User owner;
        private String name;
        private Folder parentFolder;
        private LocalDateTime createdAt;

        public FolderBuilder id(Long id) { this.id = id; return this; }
        public FolderBuilder owner(User owner) { this.owner = owner; return this; }
        public FolderBuilder name(String name) { this.name = name; return this; }
        public FolderBuilder parentFolder(Folder parentFolder) { this.parentFolder = parentFolder; return this; }
        public FolderBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public Folder build() {
            return new Folder(id, owner, name, parentFolder, createdAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Folder getParentFolder() { return parentFolder; }
    public void setParentFolder(Folder parentFolder) { this.parentFolder = parentFolder; }

    public List<Folder> getSubFolders() { return subFolders; }
    public void setSubFolders(List<Folder> subFolders) { this.subFolders = subFolders; }

    public List<Document> getDocuments() { return documents; }
    public void setDocuments(List<Document> documents) { this.documents = documents; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
