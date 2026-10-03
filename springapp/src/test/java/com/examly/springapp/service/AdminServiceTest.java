package com.examly.springapp.service;

import com.examly.springapp.exception.UnauthorizedAccessException;
import com.examly.springapp.model.Role;
import com.examly.springapp.model.User;
import com.examly.springapp.repository.DocumentRepository;
import com.examly.springapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private ActivityLogService activityLogService;

    @Mock
    private QuotaService quotaService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AdminServiceImpl adminService;

    private User admin;
    private User regularUser;

    @BeforeEach
    void setUp() {
        admin = User.builder()
                .id(1L)
                .name("Admin")
                .email("admin@example.com")
                .passwordHash("pass")
                .role(Role.ADMIN)
                .createdAt(LocalDateTime.now())
                .build();

        regularUser = User.builder()
                .id(2L)
                .name("Bob")
                .email("bob@example.com")
                .passwordHash("pass")
                .role(Role.USER)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void testAdminCannotDeleteOwnAccount() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));

        assertThrows(UnauthorizedAccessException.class,
                () -> adminService.deleteUser(1L, "admin@example.com"));
        verify(userRepository, never()).delete(any());
    }

    @Test
    void testAdminCanDeleteOtherUser() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(regularUser));

        adminService.deleteUser(2L, "admin@example.com");

        verify(userRepository, times(1)).delete(regularUser);
    }

    @Test
    void testResetPassword() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(regularUser));
        when(passwordEncoder.encode("newPassword123")).thenReturn("encodedNewPass");

        adminService.resetPassword(2L, "newPassword123");

        assertEquals("encodedNewPass", regularUser.getPasswordHash());
        verify(userRepository, times(1)).save(regularUser);
    }

    @Test
    void testConfigureQuota() {
        adminService.configureQuota(1048576000L);
        verify(quotaService, times(1)).setDefaultQuota(1048576000L);
    }
}
