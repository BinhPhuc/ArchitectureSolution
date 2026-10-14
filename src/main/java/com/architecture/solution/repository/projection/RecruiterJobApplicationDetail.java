package com.architecture.solution.repository.projection;

import com.architecture.solution.enums.ApplicationStatus;
import lombok.*;

import java.time.Instant;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecruiterJobApplicationDetail {
    private String id;
    private String jobId;
    private String candidateId;
    private String cvFileId;
    private ApplicationStatus status;
    private Instant createdAt;
    private String originalFileName;
    private String candidateDisplayedName;
    private String email;
    private String phone;
    private String bio;
}
