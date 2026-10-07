package com.architecture.solution.dto.job.request;

import com.architecture.solution.annotation.ValueOfEnum;
import com.architecture.solution.enums.JobStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateJobStatusRequest {
    @ValueOfEnum(enumClass = JobStatus.class)
    @NotNull
    private String status;
}
