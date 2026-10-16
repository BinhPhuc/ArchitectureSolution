package com.architecture.solution.repository;

import com.architecture.solution.entity.JobApplication;
import com.architecture.solution.repository.projection.RecruiterJobApplicationSummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, String> {
    boolean existsByJobIdAndCandidateId(String jobId, String candidateId);

    Optional<JobApplication> findByIdAndIsDeletedFalse(String id);

    @Query("""
                SELECT new com.architecture.solution.repository.projection.RecruiterJobApplicationSummary(
                    ja.id, ja.jobId, ja.candidateId, ja.cvFileId, ja.status, f.originalFilename, u.displayedName, u.email
                )
                FROM JobApplication ja
                JOIN File f ON ja.cvFileId = f.id
                JOIN User u ON ja.candidateId = u.id
                WHERE ja.isDeleted = FALSE AND f.isDeleted = FALSE AND u.isDeleted = FALSE AND ja.jobId = :jobId
            """)
    Page<RecruiterJobApplicationSummary> findRecruiterJobApplicationSummariesByJobId(String jobId, Pageable pageable);

}
