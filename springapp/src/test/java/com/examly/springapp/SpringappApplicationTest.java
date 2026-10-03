package com.examly.springapp;

import com.examly.springapp.dto.LoginRequest;
import com.examly.springapp.dto.LoginResponse;
import com.examly.springapp.dto.RegisterRequest;
import com.examly.springapp.dto.RegisterResponse;
import com.examly.springapp.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class SpringappApplicationTest {

    @Autowired
    private AuthService authService;

    @Test
    void contextLoads() {
        assertNotNull(authService);
    }

    @Test
    void testRegistrationAndLoginFlow() {
        RegisterRequest registerReq = RegisterRequest.builder()
                .name("Integration User")
                .email("integration@example.com")
                .password("password123")
                .build();

        RegisterResponse regResponse = authService.register(registerReq);
        assertNotNull(regResponse);
        assertEquals("integration@example.com", regResponse.getEmail());
        assertEquals("USER", regResponse.getRole());

        LoginRequest loginReq = LoginRequest.builder()
                .email("integration@example.com")
                .password("password123")
                .build();

        LoginResponse loginResponse = authService.login(loginReq);
        assertNotNull(loginResponse);
        assertNotNull(loginResponse.getJwt());
        assertEquals("USER", loginResponse.getRole());
    }
}
