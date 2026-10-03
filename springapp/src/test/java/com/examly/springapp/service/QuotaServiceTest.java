package com.examly.springapp.service;

import com.examly.springapp.dto.StorageUsageResponse;
import com.examly.springapp.exception.StorageQuotaExceededException;
import com.examly.springapp.model.Role;
import com.examly.springapp.model.User;
import com.examly.springapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuotaServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private QuotaServiceImpl quotaService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("Quota User")
                .email("quota@example.com")
                .passwordHash("pass")
                .role(Role.USER)
                .storageUsed(1000L)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void testCheckQuotaWithinLimit() {
        assertDoesNotThrow(() -> quotaService.checkQuota(user, 5000L));
    }

    @Test
    void testCheckQuotaExceeded() {
        assertThrows(StorageQuotaExceededException.class,
                () -> quotaService.checkQuota(user, 600000000L));
    }

    @Test
    void testAddStorageUsage() {
        when(userRepository.save(any(User.class))).thenReturn(user);

        quotaService.addStorageUsage(user, 2000L);

        assertEquals(3000L, user.getStorageUsed());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testReduceStorageUsage() {
        when(userRepository.save(any(User.class))).thenReturn(user);

        quotaService.reduceStorageUsage(user, 400L);

        assertEquals(600L, user.getStorageUsed());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testGetStorageUsage() {
        when(userRepository.findByEmail("quota@example.com")).thenReturn(Optional.of(user));

        StorageUsageResponse response = quotaService.getStorageUsage("quota@example.com");

        assertNotNull(response);
        assertEquals(1000L, response.getStorageUsed());
        assertEquals(524288000L, response.getStorageQuota());
    }
}
