package com.architecture.solution.controller;

import com.architecture.solution.dto.common.ApiResponse;
import com.architecture.solution.dto.user.request.UpdateUserProfileRequest;
import com.architecture.solution.dto.user.response.UpdateUserProfileResponse;
import com.architecture.solution.dto.user.response.UserProfileResponse;
import com.architecture.solution.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@Tag(name = "User Controller")
public class UserController {
    private final UserService userService;

    @Operation(summary = "Get user profile", description = "Get the profile information of the authenticated user.")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getUserProfile() {
        UserProfileResponse userResponse = userService.getUserProfile();
        return ResponseEntity.ok(ApiResponse.success(userResponse, "User profile retrieved successfully"));
    }

    @Operation(summary = "Update user profile", description = "Update the profile information of the authenticated user.")
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UpdateUserProfileResponse>> updateUserProfile(
            @Valid @RequestBody UpdateUserProfileRequest request) {
        UpdateUserProfileResponse response = userService.updateUserProfile(request);
        return ResponseEntity.ok(ApiResponse.success(response, "User profile updated successfully"));
    }
}
