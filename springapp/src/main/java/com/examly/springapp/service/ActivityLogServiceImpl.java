package com.examly.springapp.service;

import com.examly.springapp.dto.ActivityLogResponse;
import com.examly.springapp.exception.UserNotFoundException;
import com.examly.springapp.model.ActivityLog;
import com.examly.springapp.model.Document;
import com.examly.springapp.model.User;
import com.examly.springapp.repository.ActivityLogRepository;
import com.examly.springapp.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ActivityLogServiceImpl implements ActivityLogService {

    private final ActivityLogRepository activityLogRepository;
    private final UserRepository userRepository;

    public ActivityLogServiceImpl(ActivityLogRepository activityLogRepository, UserRepository userRepository) {
        this.activityLogRepository = activityLogRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void log(User user, Document document, String action) {
        ActivityLog log = ActivityLog.builder()
                .user(user)
                .document(document)
                .action(action)
                .timestamp(LocalDateTime.now())
                .build();
        activityLogRepository.save(log);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActivityLogResponse> getUserActivityLogs(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + userEmail));

        return activityLogRepository.findByUserOrderByTimestampDesc(user).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ActivityLogResponse> getAllActivityLogs(Pageable pageable) {
        return activityLogRepository.findAllByOrderByTimestampDesc(pageable)
                .map(this::mapToResponse);
    }

    private ActivityLogResponse mapToResponse(ActivityLog log) {
        return ActivityLogResponse.builder()
                .id(log.getId())
                .userId(log.getUser() != null ? log.getUser().getId() : null)
                .userEmail(log.getUser() != null ? log.getUser().getEmail() : null)
                .userName(log.getUser() != null ? log.getUser().getName() : null)
                .documentId(log.getDocument() != null ? log.getDocument().getId() : null)
                .documentName(log.getDocument() != null ? log.getDocument().getName() : null)
                .action(log.getAction())
                .timestamp(log.getTimestamp())
                .build();
    }
}
