package com.examly.springapp.dto;

import com.examly.springapp.model.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AdminUserRequest {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid email format")
    private String email;

    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    private Role role;

    public AdminUserRequest() {
    }

    public AdminUserRequest(String name, String email, String password, Role role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public static AdminUserRequestBuilder builder() {
        return new AdminUserRequestBuilder();
    }

    public static class AdminUserRequestBuilder {
        private String name;
        private String email;
        private String password;
        private Role role;

        public AdminUserRequestBuilder name(String name) { this.name = name; return this; }
        public AdminUserRequestBuilder email(String email) { this.email = email; return this; }
        public AdminUserRequestBuilder password(String password) { this.password = password; return this; }
        public AdminUserRequestBuilder role(Role role) { this.role = role; return this; }

        public AdminUserRequest build() {
            return new AdminUserRequest(name, email, password, role);
        }
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}
