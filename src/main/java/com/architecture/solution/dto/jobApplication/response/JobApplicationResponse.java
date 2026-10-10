package com.architecture.solution.dto.jobApplication.response;

import com.architecture.solution.enums.ApplicationStatus;
import lombok.*;

import java.time.Instant;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JobApplicationResponse {
    private String id;
    private String candidateId;
    private String jobId;
    private ApplicationStatus status;
    private String cvFileId;
    private Instant createAt;
    private Instant lastModifiedAt;
    private String lastModifiedBy;
}
