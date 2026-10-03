package com.examly.springapp.service;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class StorageServiceImpl implements StorageService {

    private static final Logger logger = LoggerFactory.getLogger(StorageServiceImpl.class);

    @Value("${app.storage.location:./uploads}")
    private String storageLocation;

    private Path rootLocation;

    @PostConstruct
    public void init() {
        try {
            rootLocation = Paths.get(storageLocation).toAbsolutePath().normalize();
            Files.createDirectories(rootLocation);
            logger.info("Storage initialized at: {}", rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage directory: " + storageLocation, e);
        }
    }

    @Override
    public String store(MultipartFile file, Long userId) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Failed to store empty or null file");
        }

        try {
            String originalFilename = file.getOriginalFilename();
            String cleanFilename = (originalFilename != null) ? Paths.get(originalFilename).getFileName().toString() : "file";
            String uniqueFilename = userId + "_" + UUID.randomUUID() + "_" + cleanFilename;

            Path destinationFile = this.rootLocation.resolve(uniqueFilename).normalize().toAbsolutePath();

            if (!destinationFile.getParent().equals(this.rootLocation)) {
                // Security check to prevent path traversal attacks
                throw new SecurityException("Cannot store file outside current storage directory");
            }

            Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);
            return destinationFile.toString();
        } catch (IOException e) {
            logger.error("Failed to store file", e);
            throw new RuntimeException("Failed to store file: " + e.getMessage(), e);
        }
    }

    @Override
    public Resource download(String fileUrl) {
        try {
            Path file = Paths.get(fileUrl);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read file or file does not exist: " + fileUrl);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error reading file path: " + fileUrl, e);
        }
    }

    @Override
    public void delete(String fileUrl) {
        if (fileUrl == null || fileUrl.trim().isEmpty()) {
            return;
        }

        try {
            Path file = Paths.get(fileUrl);
            Files.deleteIfExists(file);
        } catch (IOException e) {
            logger.warn("Could not delete file from storage: {}", fileUrl, e);
        }
    }

    @Override
    public boolean exists(String fileUrl) {
        if (fileUrl == null) {
            return false;
        }
        File file = new File(fileUrl);
        return file.exists();
    }
}
