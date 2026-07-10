package com.examly.springapp.service;

import com.examly.springapp.model.Document;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface DocumentService {
    Document saveDocument(String name, String email, MultipartFile file) throws IOException;
    Optional<Document> getDocument(Long id);
    List<Document> getAllDocuments();
}