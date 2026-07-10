package com.examly.springapp.service;

import com.examly.springapp.model.Document;
import com.examly.springapp.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DocumentServiceImpl implements DocumentService {

    @Autowired
    private DocumentRepository documentRepository;

    @Override
    public Document saveDocument(String filename, String email, byte[] fileData) {
        Document doc = new Document();
        doc.setFilename(filename);
        doc.setEmail(email);
        doc.setFileData(fileData);
        return documentRepository.save(doc);
    }

    @Override
    public Optional<Document> getDocument(Long id) {
        return documentRepository.findById(id);
    }

    @Override
    public List<Document> getAllDocuments() {
        return documentRepository.findAll();
    }

    @Override
    public List<Map<String, Object>> getAllMetadata() {
        return documentRepository.findAll().stream().map(doc -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", doc.getId());
            map.put("filename", doc.getFilename());
            map.put("email", doc.getEmail());
            return map;
        }).collect(Collectors.toList());
    }
}
