package com.examly.springapp.controller;

import com.examly.springapp.dto.ActivityLogResponse;
import com.examly.springapp.service.ActivityLogService;
import com.examly.springapp.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/activity-logs")
@Tag(name = "Activity Logs", description = "Activity logging APIs for user actions")
@SecurityRequirement(name = "BearerAuth")
public class ActivityLogController {

    private final ActivityLogService activityLogService;

    public ActivityLogController(ActivityLogService activityLogService) {
        this.activityLogService = activityLogService;
    }

    @GetMapping
    @Operation(summary = "Get user activity logs", description = "Returns chronological list of actions performed by the authenticated user")
    public ResponseEntity<List<ActivityLogResponse>> getMyActivityLogs() {
        String email = SecurityUtil.getCurrentUserEmail();
        return ResponseEntity.ok(activityLogService.getUserActivityLogs(email));
    }
}
