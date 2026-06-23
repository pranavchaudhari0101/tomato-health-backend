package com.tomatohealth.controller;

import com.tomatohealth.dto.auth.ChangePasswordRequest;
import com.tomatohealth.dto.user.UpdateProfileRequest;
import com.tomatohealth.dto.user.UserProfileResponse;
import com.tomatohealth.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST controller for user profile management.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "User profile management endpoints")
public class UserController {

    private final UserService userService;

    /**
     * Get the authenticated user's profile.
     *
     * @return user profile
     */
    @GetMapping("/profile")
    @Operation(summary = "Get user profile", description = "Get the current authenticated user's profile")
    public ResponseEntity<UserProfileResponse> getProfile() {
        String username = getCurrentUsername();
        return ResponseEntity.ok(userService.getUserProfile(username));
    }

    /**
     * Update the authenticated user's profile.
     *
     * @param request profile update data
     * @return updated user profile
     */
    @PutMapping("/profile")
    @Operation(summary = "Update profile", description = "Update the current user's profile information")
    public ResponseEntity<UserProfileResponse> updateProfile(@RequestBody UpdateProfileRequest request) {
        String username = getCurrentUsername();
        return ResponseEntity.ok(userService.updateProfile(username, request));
    }

    /**
     * Change the authenticated user's password.
     *
     * @param request password change data
     * @return success message
     */
    @PutMapping("/change-password")
    @Operation(summary = "Change password", description = "Change the current user's password")
    public ResponseEntity<Map<String, String>> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        String username = getCurrentUsername();
        userService.changePassword(username, request);
        return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
    }

    /**
     * Get the currently authenticated username from SecurityContext.
     */
    private String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
