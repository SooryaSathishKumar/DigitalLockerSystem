package com.examly.springapp.service;

import com.examly.springapp.model.Document;
import com.examly.springapp.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class DocumentServiceImpl implements DocumentService {

    @Autowired
    private DocumentRepository documentRepository;

    @Override
    public Document saveDocument(String name, String email, MultipartFile file) throws IOException {
        byte[] bytes = (file != null) ? file.getBytes() : new byte[0];
        String contentType = (file != null) ? file.getContentType() : "application/octet-stream";
        return documentRepository.save(new Document(name, email, contentType, bytes));
    }

    @Override
    public Document saveDocument(MultipartFile file, String email) throws IOException {
        String name = (file != null) ? file.getOriginalFilename() : "document";
        return saveDocument(name, email, file);
    }

    @Override
    public Optional<Document> getDocument(Long id) {
        return documentRepository.findById(id);
    }

    @Override
    public List<Document> getAllDocuments() {
        return documentRepository.findAll();
    }
}