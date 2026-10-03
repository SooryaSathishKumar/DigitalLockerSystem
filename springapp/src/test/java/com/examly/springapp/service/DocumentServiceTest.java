package com.examly.springapp.service;

import com.examly.springapp.dto.DocumentResponse;
import com.examly.springapp.exception.FileSizeExceededException;
import com.examly.springapp.exception.StorageQuotaExceededException;
import com.examly.springapp.exception.UnauthorizedAccessException;
import com.examly.springapp.exception.UnsupportedFileTypeException;
import com.examly.springapp.model.Document;
import com.examly.springapp.model.Role;
import com.examly.springapp.model.User;
import com.examly.springapp.repository.DocumentRepository;
import com.examly.springapp.repository.FolderRepository;
import com.examly.springapp.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FolderRepository folderRepository;

    @Mock
    private StorageService storageService;

    @Mock
    private QuotaService quotaService;

    @Mock
    private ActivityLogService activityLogService;

    @InjectMocks
    private DocumentServiceImpl documentService;

    private User owner;
    private User otherUser;
    private Document testDoc;

    @BeforeEach
    void setUp() {
        owner = User.builder()
                .id(1L)
                .name("Owner User")
                .email("owner@example.com")
                .passwordHash("pass")
                .role(Role.USER)
                .storageUsed(1000L)
                .createdAt(LocalDateTime.now())
                .build();

        otherUser = User.builder()
                .id(2L)
                .name("Other User")
                .email("other@example.com")
                .passwordHash("pass")
                .role(Role.USER)
                .storageUsed(0L)
                .createdAt(LocalDateTime.now())
                .build();

        testDoc = Document.builder()
                .id(10L)
                .name("sample.pdf")
                .fileType("PDF")
                .fileUrl("uploads/sample.pdf")
                .size(1024L)
                .owner(owner)
                .isArchived(false)
                .uploadedAt(LocalDateTime.now())
                .build();

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("owner@example.com", "password", Collections.emptyList())
        );
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testUploadDocumentSuccess() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "contract.pdf", "application/pdf", "Hello PDF".getBytes()
        );

        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(owner));
        when(quotaService.getMaxFileSize()).thenReturn(52428800L);
        doNothing().when(quotaService).checkQuota(eq(owner), anyLong());
        when(storageService.store(eq(file), eq(owner.getId()))).thenReturn("uploads/1_contract.pdf");
        when(documentRepository.save(any(Document.class))).thenReturn(testDoc);

        DocumentResponse response = documentService.uploadDocument(file);

        assertNotNull(response);
        verify(quotaService, times(1)).addStorageUsage(eq(owner), eq(file.getSize()));
        verify(activityLogService, times(1)).log(eq(owner), any(Document.class), eq("UPLOAD"));
    }

    @Test
    void testUploadDocumentInvalidFileTypeThrowsException() {
        MockMultipartFile exeFile = new MockMultipartFile(
                "file", "script.exe", "application/octet-stream", "evil binary".getBytes()
        );

        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(owner));
        when(quotaService.getMaxFileSize()).thenReturn(52428800L);

        assertThrows(UnsupportedFileTypeException.class, () -> documentService.uploadDocument(exeFile));
        verify(storageService, never()).store(any(), any());
    }

    @Test
    void testUploadDocumentFileSizeExceededThrowsException() {
        MockMultipartFile hugeFile = new MockMultipartFile(
                "file", "huge.pdf", "application/pdf", new byte[100]
        );

        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(owner));
        when(quotaService.getMaxFileSize()).thenReturn(50L); // Smaller limit than file size

        assertThrows(FileSizeExceededException.class, () -> documentService.uploadDocument(hugeFile));
    }

    @Test
    void testUploadDocumentStorageQuotaExceededThrowsException() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.pdf", "application/pdf", "content".getBytes()
        );

        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(owner));
        when(quotaService.getMaxFileSize()).thenReturn(52428800L);
        doThrow(new StorageQuotaExceededException("Quota exceeded"))
                .when(quotaService).checkQuota(eq(owner), anyLong());

        assertThrows(StorageQuotaExceededException.class, () -> documentService.uploadDocument(file));
        verify(storageService, never()).store(any(), any());
    }

    @Test
    void testDownloadDocumentOwnershipEnforced() {
        // Log in as other user
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("other@example.com", "password", Collections.emptyList())
        );

        when(documentRepository.findById(10L)).thenReturn(Optional.of(testDoc));
        when(userRepository.findByEmail("other@example.com")).thenReturn(Optional.of(otherUser));

        assertThrows(UnauthorizedAccessException.class, () -> documentService.downloadDocument(10L));
    }

    @Test
    void testDownloadDocumentSuccessForOwner() {
        when(documentRepository.findById(10L)).thenReturn(Optional.of(testDoc));
        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(owner));
        when(storageService.download("uploads/sample.pdf")).thenReturn(new ByteArrayResource("pdf content".getBytes()));

        Resource resource = documentService.downloadDocument(10L);

        assertNotNull(resource);
        verify(activityLogService, times(1)).log(eq(owner), eq(testDoc), eq("DOWNLOAD"));
    }

    @Test
    void testDeleteDocumentSoftDeletes() {
        when(documentRepository.findById(10L)).thenReturn(Optional.of(testDoc));
        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(owner));

        documentService.deleteDocument(10L);

        assertTrue(testDoc.getIsArchived());
        verify(documentRepository, times(1)).save(testDoc);
        verify(activityLogService, times(1)).log(eq(owner), eq(testDoc), eq("DELETE"));
    }
}
