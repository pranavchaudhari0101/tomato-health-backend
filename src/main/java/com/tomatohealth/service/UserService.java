package com.tomatohealth.service;

import com.tomatohealth.dto.auth.ChangePasswordRequest;
import com.tomatohealth.dto.user.UpdateProfileRequest;
import com.tomatohealth.dto.user.UserProfileResponse;
import com.tomatohealth.entity.User;
import com.tomatohealth.exception.BadRequestException;
import com.tomatohealth.exception.ResourceNotFoundException;
import com.tomatohealth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for user profile management operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Get the profile of the specified user.
     *
     * @param username the username
     * @return user profile response
     */
    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfile(String username) {
        User user = findUserByUsername(username);
        return mapToProfileResponse(user);
    }

    /**
     * Update the profile of the specified user.
     *
     * @param username the username
     * @param request  profile update data
     * @return updated user profile response
     */
    @Transactional
    public UserProfileResponse updateProfile(String username, UpdateProfileRequest request) {
        User user = findUserByUsername(username);

        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }

        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new BadRequestException("Email is already in use: " + request.getEmail());
            }
            user.setEmail(request.getEmail());
        }

        userRepository.save(user);
        log.info("Profile updated for user: {}", username);

        return mapToProfileResponse(user);
    }

    /**
     * Change the password for the specified user.
     *
     * @param username the username
     * @param request  password change data
     */
    @Transactional
    public void changePassword(String username, ChangePasswordRequest request) {
        User user = findUserByUsername(username);

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        log.info("Password changed for user: {}", username);
    }

    /**
     * Find a user by username or throw ResourceNotFoundException.
     */
    private User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
    }

    /**
     * Map User entity to UserProfileResponse DTO.
     */
    private UserProfileResponse mapToProfileResponse(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .avatarUrl(user.getAvatarUrl())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
