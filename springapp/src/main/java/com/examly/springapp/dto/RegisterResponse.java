package com.examly.springapp.dto;

public class RegisterResponse {

    private String message;
    private Long id;
    private String name;
    private String email;
    private String role;

    public RegisterResponse() {
    }

    public RegisterResponse(String message, Long id, String name, String email, String role) {
        this.message = message;
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public static RegisterResponseBuilder builder() {
        return new RegisterResponseBuilder();
    }

    public static class RegisterResponseBuilder {
        private String message;
        private Long id;
        private String name;
        private String email;
        private String role;

        public RegisterResponseBuilder message(String message) { this.message = message; return this; }
        public RegisterResponseBuilder id(Long id) { this.id = id; return this; }
        public RegisterResponseBuilder name(String name) { this.name = name; return this; }
        public RegisterResponseBuilder email(String email) { this.email = email; return this; }
        public RegisterResponseBuilder role(String role) { this.role = role; return this; }

        public RegisterResponse build() {
            return new RegisterResponse(message, id, name, email, role);
        }
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
