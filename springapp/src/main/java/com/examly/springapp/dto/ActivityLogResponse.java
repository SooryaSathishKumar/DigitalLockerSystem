package com.examly.springapp.dto;

import java.time.LocalDateTime;

public class ActivityLogResponse {

    private Long id;
    private Long userId;
    private String userEmail;
    private String userName;
    private Long documentId;
    private String documentName;
    private String action;
    private LocalDateTime timestamp;

    public ActivityLogResponse() {
    }

    public ActivityLogResponse(Long id, Long userId, String userEmail, String userName, Long documentId, String documentName, String action, LocalDateTime timestamp) {
        this.id = id;
        this.userId = userId;
        this.userEmail = userEmail;
        this.userName = userName;
        this.documentId = documentId;
        this.documentName = documentName;
        this.action = action;
        this.timestamp = timestamp;
    }

    public static ActivityLogResponseBuilder builder() {
        return new ActivityLogResponseBuilder();
    }

    public static class ActivityLogResponseBuilder {
        private Long id;
        private Long userId;
        private String userEmail;
        private String userName;
        private Long documentId;
        private String documentName;
        private String action;
        private LocalDateTime timestamp;

        public ActivityLogResponseBuilder id(Long id) { this.id = id; return this; }
        public ActivityLogResponseBuilder userId(Long userId) { this.userId = userId; return this; }
        public ActivityLogResponseBuilder userEmail(String userEmail) { this.userEmail = userEmail; return this; }
        public ActivityLogResponseBuilder userName(String userName) { this.userName = userName; return this; }
        public ActivityLogResponseBuilder documentId(Long documentId) { this.documentId = documentId; return this; }
        public ActivityLogResponseBuilder documentName(String documentName) { this.documentName = documentName; return this; }
        public ActivityLogResponseBuilder action(String action) { this.action = action; return this; }
        public ActivityLogResponseBuilder timestamp(LocalDateTime timestamp) { this.timestamp = timestamp; return this; }

        public ActivityLogResponse build() {
            return new ActivityLogResponse(id, userId, userEmail, userName, documentId, documentName, action, timestamp);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public Long getDocumentId() { return documentId; }
    public void setDocumentId(Long documentId) { this.documentId = documentId; }

    public String getDocumentName() { return documentName; }
    public void setDocumentName(String documentName) { this.documentName = documentName; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
