package com.examly.springapp.service;

import java.util.List;

import com.examly.springapp.dto.DocumentResponse;

public interface ArchiveService {

    void archiveDocument(String userEmail, Long documentId);

    DocumentResponse restoreDocument(String userEmail, Long documentId);

    List<DocumentResponse> getArchivedDocuments(String userEmail);

    void permanentlyDelete(String userEmail, Long documentId);
}
