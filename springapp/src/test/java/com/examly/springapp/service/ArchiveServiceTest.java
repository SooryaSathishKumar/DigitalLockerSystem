package com.examly.springapp.service;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.examly.springapp.exception.UnauthorizedAccessException;
import com.examly.springapp.model.Document;
import com.examly.springapp.model.Role;
import com.examly.springapp.model.User;
import com.examly.springapp.repository.DocumentRepository;
import com.examly.springapp.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class ArchiveServiceTest {

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ActivityLogService activityLogService;

    @Mock
    private StorageService storageService;

    @Mock
    private QuotaService quotaService;

    @InjectMocks
    private ArchiveServiceImpl archiveService;

    @Test
    void permanentlyDeleteRejectsDifferentUser() {
        User owner = User.builder().id(1L).email("owner@example.com").role(Role.USER).build();
        User otherUser = User.builder().id(2L).email("other@example.com").role(Role.USER).build();
        Document document = Document.builder()
                .id(10L)
                .owner(owner)
                .fileUrl("uploads/document.pdf")
                .size(100L)
                .build();

        when(userRepository.findByEmail("other@example.com")).thenReturn(Optional.of(otherUser));
        when(documentRepository.findById(10L)).thenReturn(Optional.of(document));

        UnauthorizedAccessException exception = assertThrows(UnauthorizedAccessException.class,
            () -> archiveService.permanentlyDelete("other@example.com", 10L));
        org.junit.jupiter.api.Assertions.assertNotNull(exception);
        verifyNoInteractions(storageService, quotaService, activityLogService);
        verify(documentRepository, never()).delete(any(Document.class));
    }

    @Test
    void permanentlyDeleteAllowsAdmin() {
        User owner = User.builder().id(1L).email("owner@example.com").role(Role.USER).build();
        User admin = User.builder().id(2L).email("admin@example.com").role(Role.ADMIN).build();
        Document document = Document.builder()
                .id(10L)
                .owner(owner)
                .fileUrl("uploads/document.pdf")
                .size(100L)
                .build();

        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(admin));
        when(documentRepository.findById(10L)).thenReturn(Optional.of(document));

        archiveService.permanentlyDelete("admin@example.com", 10L);

        verify(storageService).delete("uploads/document.pdf");
        verify(quotaService).reduceStorageUsage(owner, 100L);
        verify(activityLogService).log(owner, document, "DELETE");
        verify(documentRepository).delete(document);
    }
}
