package com.examly.springapp.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    String store(MultipartFile file, Long userId);

    Resource download(String fileUrl);

    void delete(String fileUrl);

    boolean exists(String fileUrl);
}
