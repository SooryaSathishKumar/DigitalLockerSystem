package com.examly.springapp.controller;

import com.examly.springapp.dto.DocumentResponse;
import com.examly.springapp.security.JwtAuthenticationFilter;
import com.examly.springapp.security.JwtService;
import com.examly.springapp.service.ArchiveService;
import com.examly.springapp.service.DocumentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = DocumentController.class)
@AutoConfigureMockMvc(addFilters = false)
class DocumentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DocumentService documentService;

    @MockBean
    private ArchiveService archiveService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    @WithMockUser(username = "user@example.com", roles = {"USER"})
    void testGetDocumentsEndpoint() throws Exception {
        DocumentResponse doc = DocumentResponse.builder()
                .id(1L)
                .name("file.pdf")
                .fileType("PDF")
                .size(1024L)
                .isArchived(false)
                .uploadedAt(LocalDateTime.now())
                .build();

        when(documentService.getMyDocuments()).thenReturn(Collections.singletonList(doc));

        mockMvc.perform(get("/api/documents")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("file.pdf"))
                .andExpect(jsonPath("$[0].fileType").value("PDF"));
    }
}
