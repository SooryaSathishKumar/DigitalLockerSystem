package com.examly.springapp.service;

import com.examly.springapp.dto.ActivityLogResponse;
import com.examly.springapp.model.Document;
import com.examly.springapp.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ActivityLogService {

    void log(User user, Document document, String action);

    List<ActivityLogResponse> getUserActivityLogs(String userEmail);

    Page<ActivityLogResponse> getAllActivityLogs(Pageable pageable);
}
