package com.examly.springapp.service;

import com.examly.springapp.dto.FolderRequest;
import com.examly.springapp.dto.FolderResponse;
import com.examly.springapp.model.Folder;
import com.examly.springapp.model.Role;
import com.examly.springapp.model.User;
import com.examly.springapp.repository.FolderRepository;
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
class FolderServiceTest {

    @Mock
    private FolderRepository folderRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FolderServiceImpl folderService;

    private User user;
    private Folder folder;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("Alice")
                .email("alice@example.com")
                .passwordHash("hash")
                .role(Role.USER)
                .createdAt(LocalDateTime.now())
                .build();

        folder = Folder.builder()
                .id(5L)
                .name("Work Documents")
                .owner(user)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void testCreateFolder() {
        FolderRequest request = FolderRequest.builder()
                .name("Work Documents")
                .build();

        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(user));
        when(folderRepository.save(any(Folder.class))).thenReturn(folder);

        FolderResponse response = folderService.createFolder("alice@example.com", request);

        assertNotNull(response);
        assertEquals("Work Documents", response.getName());
        verify(folderRepository, times(1)).save(any(Folder.class));
    }

    @Test
    void testRenameFolder() {
        FolderRequest request = FolderRequest.builder()
                .name("Personal Documents")
                .build();

        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(user));
        when(folderRepository.findById(5L)).thenReturn(Optional.of(folder));
        when(folderRepository.save(any(Folder.class))).thenReturn(folder);

        FolderResponse response = folderService.renameFolder("alice@example.com", 5L, request);

        assertNotNull(response);
        assertEquals("Personal Documents", folder.getName());
    }

    @Test
    void testDeleteFolder() {
        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(user));
        when(folderRepository.findById(5L)).thenReturn(Optional.of(folder));

        folderService.deleteFolder("alice@example.com", 5L);

        verify(folderRepository, times(1)).delete(folder);
    }
}
