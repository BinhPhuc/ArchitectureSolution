package com.architecture.solution.repository.projection;

import com.architecture.solution.enums.ApplicationStatus;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecruiterJobApplicationSummary {
    private String id;

    private String jobId;

    private String candidateId;

    private String cvFileId;

    private ApplicationStatus status;

    private String originalFileName;

    private String candidateDisplayedName;

    private String email;
}
