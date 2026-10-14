package com.architecture.solution.controller;

import com.architecture.solution.controller.docs.UserApi;
import com.architecture.solution.dto.common.ApiResponse;
import com.architecture.solution.dto.user.request.ChangePasswordRequest;
import com.architecture.solution.dto.user.request.UpdateUserProfileRequest;
import com.architecture.solution.dto.user.response.UpdateUserProfileResponse;
import com.architecture.solution.dto.user.response.UserProfileResponse;
import com.architecture.solution.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController implements UserApi {
    private final UserService userService;

    @Override
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getUserProfile() {
        UserProfileResponse userResponse = userService.getUserProfile();
        return ResponseEntity.ok(ApiResponse.success(userResponse, "User profile retrieved successfully"));
    }

    @Override
    @PostMapping("/me")
    public ResponseEntity<ApiResponse<UpdateUserProfileResponse>> updateUserProfile(
            @Valid @RequestBody UpdateUserProfileRequest request
    ) {
        UpdateUserProfileResponse response = userService.updateUserProfile(request);
        return ResponseEntity.ok(ApiResponse.success(response, "User profile updated successfully"));
    }

    @Override
    @PostMapping("/me/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        userService.changePassword(request);
        return ResponseEntity.ok(ApiResponse.success(null, "Password changed successfully"));
    }
}
