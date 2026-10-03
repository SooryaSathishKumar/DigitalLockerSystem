package com.examly.springapp.service;

import com.examly.springapp.dto.PasswordChangeRequest;
import com.examly.springapp.dto.UserResponse;
import com.examly.springapp.dto.UserUpdateRequest;
import com.examly.springapp.exception.DuplicateEmailException;
import com.examly.springapp.exception.UserNotFoundException;
import com.examly.springapp.model.User;
import com.examly.springapp.repository.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getProfile(String email) {
        User user = getUserByEmail(email);
        return mapToResponse(user);
    }

    @Override
    public UserResponse updateProfile(String currentEmail, UserUpdateRequest request) {
        User user = getUserByEmail(currentEmail);
        String newEmail = request.getEmail().trim().toLowerCase();

        if (!user.getEmail().equalsIgnoreCase(newEmail)) {
            if (userRepository.existsByEmail(newEmail)) {
                throw new DuplicateEmailException("Email " + newEmail + " is already in use by another account");
            }
            user.setEmail(newEmail);
        }

        user.setName(request.getName().trim());
        User updated = userRepository.save(user);
        return mapToResponse(updated);
    }

    @Override
    public void changePassword(String email, PasswordChangeRequest request) {
        User user = getUserByEmail(email);

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("Current password provided is incorrect");
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
    }

    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .storageUsed(user.getStorageUsed() != null ? user.getStorageUsed() : 0L)
                .createdAt(user.getCreatedAt())
                .build();
    }
}
