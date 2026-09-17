package com.architecture.solution.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(
        name = "job_categories",
        uniqueConstraints = @UniqueConstraint(columnNames = {"job_id", "category_id"})
)
public class JobCategory extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    private String id;

    @Column(name = "job_id", length = 36, nullable = false)
    private String jobId;

    @Column(name = "category_id", length = 36, nullable = false)
    private String categoryId;
}
