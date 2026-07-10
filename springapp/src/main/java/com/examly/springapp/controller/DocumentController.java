package com.examly.springapp.controller;

import com.examly.springapp.model.Document;
import com.examly.springapp.service.DocumentService;
import com.examly.springapp.exception.DocumentNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/documents")
@CrossOrigin(origins = "*")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @PostMapping("/upload")
    public ResponseEntity<Document> uploadDocument(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam("file") MultipartFile file) {
        try {
            String finalName = (name == null || name.isEmpty()) ? file.getOriginalFilename() : name;
            byte[] fileData = file.getBytes();
            Document savedDoc = documentService.saveDocument(finalName, email, fileData);
            return new ResponseEntity<>(savedDoc, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/list")
    public ResponseEntity<List<Document>> getAllDocuments() {
        return ResponseEntity.ok(documentService.getAllDocuments());
    }

    @GetMapping("/metadata")
    public ResponseEntity<List<Map<String, Object>>> getAllMetadata() {
        return ResponseEntity.ok(documentService.getAllMetadata());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Document> getDocumentById(@PathVariable Long id) {
        Document doc = documentService.getDocument(id)
                .orElseThrow(DocumentNotFoundException::new);
        return ResponseEntity.ok(doc);
    }
}
