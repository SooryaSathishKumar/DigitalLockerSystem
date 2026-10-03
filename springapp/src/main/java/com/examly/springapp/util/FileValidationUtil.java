package com.examly.springapp.util;

import com.examly.springapp.exception.FileSizeExceededException;
import com.examly.springapp.exception.UnsupportedFileTypeException;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class FileValidationUtil {

    public static final long MAX_FILE_SIZE_BYTES = 50 * 1024 * 1024L; // 50 MB

    private static final Set<String> ALLOWED_EXTENSIONS = new HashSet<>(Arrays.asList(
            "pdf", "docx", "jpeg", "jpg", "png", "txt"
    ));

    private static final Set<String> ALLOWED_CONTENT_TYPES = new HashSet<>(Arrays.asList(
            "application/pdf",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/msword",
            "image/jpeg",
            "image/jpg",
            "image/png",
            "text/plain"
    ));

    private FileValidationUtil() {
        // Private constructor for utility class
    }

    public static void validateFile(MultipartFile file, long maxSizeBytes) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be null or empty");
        }

        if (file.getSize() > maxSizeBytes) {
            throw new FileSizeExceededException("File size exceeds the maximum limit of " + (maxSizeBytes / (1024 * 1024)) + " MB");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new UnsupportedFileTypeException("Invalid file name or missing extension");
        }

        String extension = getFileExtension(originalFilename).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new UnsupportedFileTypeException("Unsupported file extension: " + extension + ". Supported extensions are: PDF, DOCX, JPEG, JPG, PNG, TXT");
        }

        String contentType = file.getContentType();
        if (contentType != null && !contentType.isEmpty() && !contentType.equals("application/octet-stream")) {
            if (!ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
                // If extension matched, but content-type is non-standard, verify extension match
                if (!ALLOWED_EXTENSIONS.contains(extension)) {
                    throw new UnsupportedFileTypeException("Unsupported file content type: " + contentType);
                }
            }
        }
    }

    public static String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }
}
