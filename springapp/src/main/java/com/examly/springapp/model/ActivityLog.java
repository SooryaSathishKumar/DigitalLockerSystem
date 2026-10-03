package com.examly.springapp.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "activity_logs")
public class ActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id")
    private Document document;

    @Column(nullable = false)
    private String action;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    public ActivityLog() {
    }

    public ActivityLog(Long id, User user, Document document, String action, LocalDateTime timestamp) {
        this.id = id;
        this.user = user;
        this.document = document;
        this.action = action;
        this.timestamp = timestamp;
    }

    @PrePersist
    protected void onCreate() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }

    public static ActivityLogBuilder builder() {
        return new ActivityLogBuilder();
    }

    public static class ActivityLogBuilder {
        private Long id;
        private User user;
        private Document document;
        private String action;
        private LocalDateTime timestamp;

        public ActivityLogBuilder id(Long id) { this.id = id; return this; }
        public ActivityLogBuilder user(User user) { this.user = user; return this; }
        public ActivityLogBuilder document(Document document) { this.document = document; return this; }
        public ActivityLogBuilder action(String action) { this.action = action; return this; }
        public ActivityLogBuilder timestamp(LocalDateTime timestamp) { this.timestamp = timestamp; return this; }

        public ActivityLog build() {
            return new ActivityLog(id, user, document, action, timestamp);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Document getDocument() { return document; }
    public void setDocument(Document document) { this.document = document; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
