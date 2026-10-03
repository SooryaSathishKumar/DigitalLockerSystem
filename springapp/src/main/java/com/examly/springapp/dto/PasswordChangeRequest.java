package com.examly.springapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PasswordChangeRequest {

    @NotBlank(message = "Current password is required")
    private String currentPassword;

    @NotBlank(message = "New password is required")
    @Size(min = 6, message = "New password must be at least 6 characters long")
    private String newPassword;

    public PasswordChangeRequest() {
    }

    public PasswordChangeRequest(String currentPassword, String newPassword) {
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
    }

    public static PasswordChangeRequestBuilder builder() {
        return new PasswordChangeRequestBuilder();
    }

    public static class PasswordChangeRequestBuilder {
        private String currentPassword;
        private String newPassword;

        public PasswordChangeRequestBuilder currentPassword(String currentPassword) { this.currentPassword = currentPassword; return this; }
        public PasswordChangeRequestBuilder newPassword(String newPassword) { this.newPassword = newPassword; return this; }

        public PasswordChangeRequest build() {
            return new PasswordChangeRequest(currentPassword, newPassword);
        }
    }

    public String getCurrentPassword() { return currentPassword; }
    public void setCurrentPassword(String currentPassword) { this.currentPassword = currentPassword; }

    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
}
