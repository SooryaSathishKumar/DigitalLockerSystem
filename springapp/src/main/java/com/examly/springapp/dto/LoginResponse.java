package com.examly.springapp.dto;

public class LoginResponse {

    private String jwt;
    private String role;

    public LoginResponse() {
    }

    public LoginResponse(String jwt, String role) {
        this.jwt = jwt;
        this.role = role;
    }

    public static LoginResponseBuilder builder() {
        return new LoginResponseBuilder();
    }

    public static class LoginResponseBuilder {
        private String jwt;
        private String role;

        public LoginResponseBuilder jwt(String jwt) { this.jwt = jwt; return this; }
        public LoginResponseBuilder role(String role) { this.role = role; return this; }

        public LoginResponse build() {
            return new LoginResponse(jwt, role);
        }
    }

    public String getJwt() { return jwt; }
    public void setJwt(String jwt) { this.jwt = jwt; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
