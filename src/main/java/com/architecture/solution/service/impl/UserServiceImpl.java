package com.architecture.solution.service.impl;

import com.architecture.solution.dto.ProfileResponse;
import com.architecture.solution.dto.candidate.response.CandidateProfileResponse;
import com.architecture.solution.dto.recruiter.response.RecruiterProfileResponse;
import com.architecture.solution.dto.user.request.ChangePasswordRequest;
import com.architecture.solution.dto.user.request.UpdateUserProfileRequest;
import com.architecture.solution.dto.user.response.UpdateUserProfileResponse;
import com.architecture.solution.dto.user.response.UserProfileResponse;
import com.architecture.solution.entity.Candidate;
import com.architecture.solution.entity.Recruiter;
import com.architecture.solution.entity.User;
import com.architecture.solution.exception.InvalidArgumentException;
import com.architecture.solution.exception.ResourceExistsException;
import com.architecture.solution.repository.CandidateRepository;
import com.architecture.solution.repository.RecruiterRepository;
import com.architecture.solution.repository.UserRepository;
import com.architecture.solution.service.UserService;
import com.architecture.solution.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final CandidateRepository candidateRepository;
    private final RecruiterRepository recruiterRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserProfileResponse getUserProfile() {
        User user = SecurityUtils.getUser();

        Optional<Candidate> candidate = candidateRepository.findByUserIdAndIsDeletedFalse(user.getId());
        CandidateProfileResponse candidateProfileResponse = candidate.map(value -> CandidateProfileResponse.builder()
                .bio(value.getBio())
                .phone(value.getPhone())
                .build()).orElse(null);
        Optional<Recruiter> recruiter = recruiterRepository.findByUserIdAndIsDeletedFalse(user.getId());
        RecruiterProfileResponse recruiterProfileResponse = recruiter.map(value -> RecruiterProfileResponse.builder()
                .companyName(value.getCompanyName())
                .build()).orElse(null);

        ProfileResponse profileResponse = ProfileResponse.builder()
                .candidateProfile(candidateProfileResponse)
                .recruiterProfile(recruiterProfileResponse)
                .build();

        return UserProfileResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .displayedName(user.getDisplayedName())
                .profiles(profileResponse)
                .build();
    }

    @Override
    public UpdateUserProfileResponse updateUserProfile(UpdateUserProfileRequest request) {
        boolean needPersist = false;
        User user = SecurityUtils.getUser();
        String newEmail = request.getEmail();
        if (StringUtils.hasText(newEmail)) {
            if (!newEmail.equals(user.getEmail()) && userRepository.existsByEmail(newEmail)) {
                throw new ResourceExistsException("User with email " + newEmail + " already exists");
            }
            user.setEmail(request.getEmail());
            needPersist = true;
        }
        String displayedName = request.getDisplayedName();
        if (StringUtils.hasText(displayedName)) {
            user.setDisplayedName(displayedName);
            needPersist = true;
        }
        if (needPersist) {
            userRepository.save(user);
        }
        return UpdateUserProfileResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .displayedName(user.getDisplayedName())
                .lastModifiedAt(user.getLastModifiedAt())
                .build();
    }

    @Override
    public void changePassword(ChangePasswordRequest request) {
        User user = SecurityUtils.getUser();
        String currentPassword = request.getCurrentPassword();
        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new InvalidArgumentException("Current password is incorrect");
        }
        String newPassword = request.getNewPassword();
        String retypePassword = request.getRetypeNewPassword();
        if (!newPassword.equals(retypePassword)) {
            throw new InvalidArgumentException("Passwords do not match");
        }
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
