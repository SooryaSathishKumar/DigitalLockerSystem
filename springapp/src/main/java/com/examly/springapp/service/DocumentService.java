package com.examly.springapp.service;

import com.examly.springapp.dto.DocumentResponse;
import com.examly.springapp.dto.DocumentUpdateRequest;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DocumentService {

    DocumentResponse uploadDocument(MultipartFile file);

    DocumentResponse uploadDocument(MultipartFile file, Long parentFolderId, String tags);

    List<DocumentResponse> getMyDocuments();

    List<DocumentResponse> searchDocuments(String name, String fileType, String tag, Long minSize, Long maxSize);

    DocumentResponse getDocumentById(Long id);

    Resource downloadDocument(Long id);

    DocumentResponse updateDocument(Long id, DocumentUpdateRequest request);

    void deleteDocument(Long id);

    String getDocumentFilename(Long id);
}
