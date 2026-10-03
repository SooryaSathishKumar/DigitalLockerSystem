package com.examly.springapp.service;

import com.examly.springapp.dto.PasswordChangeRequest;
import com.examly.springapp.dto.UserResponse;
import com.examly.springapp.dto.UserUpdateRequest;
import com.examly.springapp.model.User;

public interface UserService {

    UserResponse getProfile(String email);

    UserResponse updateProfile(String currentEmail, UserUpdateRequest request);

    void changePassword(String email, PasswordChangeRequest request);

    User getUserByEmail(String email);
}
