package com.examly.springapp.service;

import com.examly.springapp.dto.PasswordChangeRequest;
import com.examly.springapp.dto.UserResponse;
import com.examly.springapp.dto.UserUpdateRequest;
import com.examly.springapp.model.Role;
import com.examly.springapp.model.User;
import com.examly.springapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("Jane Doe")
                .email("jane@example.com")
                .passwordHash("hashedPassword")
                .role(Role.USER)
                .storageUsed(5000L)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void testGetProfile() {
        when(userRepository.findByEmail("jane@example.com")).thenReturn(Optional.of(user));

        UserResponse response = userService.getProfile("jane@example.com");

        assertNotNull(response);
        assertEquals("Jane Doe", response.getName());
        assertEquals("jane@example.com", response.getEmail());
        assertEquals(5000L, response.getStorageUsed());
    }

    @Test
    void testUpdateProfile() {
        UserUpdateRequest request = UserUpdateRequest.builder()
                .name("Jane Updated")
                .email("jane@example.com")
                .build();

        when(userRepository.findByEmail("jane@example.com")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponse response = userService.updateProfile("jane@example.com", request);

        assertNotNull(response);
        assertEquals("Jane Updated", user.getName());
    }

    @Test
    void testChangePasswordSuccess() {
        PasswordChangeRequest request = PasswordChangeRequest.builder()
                .currentPassword("oldPass")
                .newPassword("newPass123")
                .build();

        when(userRepository.findByEmail("jane@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldPass", "hashedPassword")).thenReturn(true);
        when(passwordEncoder.encode("newPass123")).thenReturn("newHashedPassword");

        userService.changePassword("jane@example.com", request);

        assertEquals("newHashedPassword", user.getPasswordHash());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testChangePasswordInvalidCurrentThrowsException() {
        PasswordChangeRequest request = PasswordChangeRequest.builder()
                .currentPassword("wrongPass")
                .newPassword("newPass123")
                .build();

        when(userRepository.findByEmail("jane@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPass", "hashedPassword")).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> userService.changePassword("jane@example.com", request));
        verify(userRepository, never()).save(user);
    }
}
