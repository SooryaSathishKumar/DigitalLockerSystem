package com.examly.springapp.controller;

import com.examly.springapp.dto.PasswordChangeRequest;
import com.examly.springapp.dto.UserResponse;
import com.examly.springapp.dto.UserUpdateRequest;
import com.examly.springapp.service.UserService;
import com.examly.springapp.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Profile", description = "User profile and account management APIs")
@SecurityRequirement(name = "BearerAuth")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    @Operation(summary = "Get user profile", description = "Returns details and storage usage of current authenticated user")
    public ResponseEntity<UserResponse> getMyProfile() {
        String email = SecurityUtil.getCurrentUserEmail();
        return ResponseEntity.ok(userService.getProfile(email));
    }

    @PutMapping("/me")
    @Operation(summary = "Update user profile", description = "Updates name and email of authenticated user")
    public ResponseEntity<UserResponse> updateProfile(@Valid @RequestBody UserUpdateRequest request) {
        String email = SecurityUtil.getCurrentUserEmail();
        return ResponseEntity.ok(userService.updateProfile(email, request));
    }

    @PutMapping("/me/password")
    @Operation(summary = "Change password", description = "Changes the authenticated user's password after verifying current password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody PasswordChangeRequest request) {
        String email = SecurityUtil.getCurrentUserEmail();
        userService.changePassword(email, request);
        return ResponseEntity.ok().build();
    }
}
