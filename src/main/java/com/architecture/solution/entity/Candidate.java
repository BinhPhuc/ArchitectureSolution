package com.architecture.solution.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "candidates")
public class Candidate extends BaseEntity {
    @Id
    @Column(name = "user_id", length = 36)
    private String userId;

    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    @Column(name = "cv_file_id", length = 36, unique = true)
    private String cvFileId;

    @Column(name = "phone")
    private String phone;
}
