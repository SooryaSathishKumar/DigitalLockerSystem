package com.examly.springapp.service;

import com.examly.springapp.model.Document;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface DocumentService {

    Document saveDocument(String filename, String email, byte[] fileData);
    Optional<Document> getDocument(Long id);
    List<Document> getAllDocuments();
    List<Map<String, Object>> getAllMetadata();
}
