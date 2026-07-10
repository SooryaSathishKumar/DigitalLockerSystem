package com.examly.springapp;

import com.examly.springapp.model.Document;
import com.examly.springapp.repository.DocumentRepository;
import com.examly.springapp.service.DocumentService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class SpringappApplicationTest {

    @Autowired
    private DocumentService documentService;

    @Autowired
    private DocumentRepository documentRepository;

    // Test 1: Save Document with valid data
    @Test
    void testSaveDocumentWithValidInput() {
        Document doc = documentService.saveDocument("test1.pdf", "user1@example.com", new byte[]{1, 2, 3});
        assertNotNull(doc.getId());
        assertEquals("test1.pdf", doc.getFilename());
    }

    // Test 2: Save Document with null data
    @Test
    void testSaveDocumentWithNullFileData() {
        Document doc = documentService.saveDocument("empty.txt", "user2@example.com", null);
        assertNotNull(doc.getId());
        assertNull(doc.getFileData());
    }

    // Test 3: Get existing document by ID
    @Test
    void testGetExistingDocumentById() {
        Document doc = documentService.saveDocument("doc.pdf", "get@test.com", new byte[]{9});
        Optional<Document> retrieved = documentService.getDocument(doc.getId());
        assertTrue(retrieved.isPresent());
        assertEquals("get@test.com", retrieved.get().getEmail());
    }

    // Test 4: Get non-existent document by ID
    @Test
    void testGetNonExistentDocumentById() {
        Optional<Document> result = documentService.getDocument(999L);
        assertFalse(result.isPresent());
    }

    // Test 5: Metadata returns correct count
    @Test
    void testGetAllMetadataCount() {
        documentService.saveDocument("m1.pdf", "m1@example.com", new byte[]{1});
        documentService.saveDocument("m2.pdf", "m2@example.com", new byte[]{2});
        List<Map<String, Object>> metadata = documentService.getAllMetadata();
        assertTrue(metadata.size() >= 2);
    }

    // Test 6: Metadata map contains expected keys
    @Test
    void testMetadataStructure() {
        documentService.saveDocument("meta.pdf", "meta@example.com", new byte[]{});
        List<Map<String, Object>> metadata = documentService.getAllMetadata();
        assertTrue(metadata.get(0).containsKey("id"));
        assertTrue(metadata.get(0).containsKey("filename"));
        assertTrue(metadata.get(0).containsKey("email"));
    }

    // Test 7: Repository save and retrieve
    @Test
    void testRepositorySaveAndFind() {
        Document doc = new Document();
        doc.setFilename("repo.docx");
        doc.setEmail("repo@repo.com");
        doc.setFileData(new byte[]{7});
        Document saved = documentRepository.save(doc);
        assertTrue(documentRepository.findById(saved.getId()).isPresent());
    }

    // Test 8: Save document with empty email
    @Test
    void testSaveWithEmptyEmail() {
        Document doc = documentService.saveDocument("blank.email", "", new byte[]{});
        assertNotNull(doc.getId());
        assertEquals("", doc.getEmail());
    }

    // Test 9: Save document with large file
    @Test
    void testSaveLargeDocument() {
        byte[] data = new byte[1024 * 1024]; // 1MB
        Document doc = documentService.saveDocument("largefile.bin", "big@file.com", data);
        assertEquals(1024 * 1024, doc.getFileData().length);
    }

    // Test 10: Save document with special characters in filename
    @Test
    void testSaveWithSpecialFilename() {
        Document doc = documentService.saveDocument("file @#$%.txt", "user@example.com", new byte[]{});
        assertEquals("file @#$%.txt", doc.getFilename());
    }

    // Test 11: Save multiple documents and verify count
    @Test
    void testMultipleDocumentSave() {
        documentService.saveDocument("multi1.txt", "a@a.com", new byte[]{1});
        documentService.saveDocument("multi2.txt", "b@b.com", new byte[]{2});
        assertTrue(documentService.getAllMetadata().size() >= 2);
    }

    // Test 12: File data integrity
    @Test
    void testFileDataIntegrity() {
        byte[] bytes = {10, 20, 30};
        Document doc = documentService.saveDocument("integrity.txt", "check@data.com", bytes);
        assertArrayEquals(bytes, doc.getFileData());
    }

    // Test 13: Save document with null email
    @Test
    void testSaveWithNullEmail() {
        Document doc = documentService.saveDocument("noemail.txt", null, new byte[]{5});
        assertNull(doc.getEmail());
    }
}
