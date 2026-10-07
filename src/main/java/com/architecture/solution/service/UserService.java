package com.architecture.solution.service;

import com.architecture.solution.dto.user.request.ChangePasswordRequest;
import com.architecture.solution.dto.user.request.UpdateUserProfileRequest;
import com.architecture.solution.dto.user.response.UpdateUserProfileResponse;
import com.architecture.solution.dto.user.response.UserProfileResponse;

public interface UserService {
    UserProfileResponse getUserProfile();

    UpdateUserProfileResponse updateUserProfile(UpdateUserProfileRequest request);

    void changePassword(ChangePasswordRequest request);
}
