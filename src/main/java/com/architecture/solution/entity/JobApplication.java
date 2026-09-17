package com.architecture.solution.entity;

import com.architecture.solution.enums.ApplicationStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(
        name = "job_applications",
        uniqueConstraints = @UniqueConstraint(columnNames = {"job_id", "candidate_id"})
)
public class JobApplication extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    private String id;

    @Column(name = "candidate_id", length = 36, nullable = false)
    private String candidateId;

    @Column(name = "job_id", length = 36, nullable = false)
    private String jobId;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ApplicationStatus status = ApplicationStatus.PENDING;

    @Column(name = "cv_url", nullable = false)
    private String cvUrl;
}
