package com.examly.springapp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserUpdateRequest {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid email format")
    private String email;

    public UserUpdateRequest() {
    }

    public UserUpdateRequest(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public static UserUpdateRequestBuilder builder() {
        return new UserUpdateRequestBuilder();
    }

    public static class UserUpdateRequestBuilder {
        private String name;
        private String email;

        public UserUpdateRequestBuilder name(String name) { this.name = name; return this; }
        public UserUpdateRequestBuilder email(String email) { this.email = email; return this; }

        public UserUpdateRequest build() {
            return new UserUpdateRequest(name, email);
        }
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
