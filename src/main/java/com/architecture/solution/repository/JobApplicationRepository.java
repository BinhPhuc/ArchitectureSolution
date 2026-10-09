package com.architecture.solution.repository;

import com.architecture.solution.entity.JobApplication;
import com.architecture.solution.repository.projection.JobApplicationByCandidateId;
import com.architecture.solution.repository.projection.JobApplicationByJobId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, String> {
    boolean existsByJobIdAndCandidateId(String jobId, String candidateId);

    @Query("""
                SELECT new com.architecture.solution.repository.projection.JobApplicationByCandidateId(
                    ja.candidateId, ja.jobId, j.title, j.description, r.companyName, j.jobType, ja.status
                )
                FROM JobApplication ja
                JOIN Job j on j.id = ja.jobId
                JOIN Recruiter r on j.recruiterId = r.userId
                WHERE ja.isDeleted = FALSE AND j.isDeleted = FALSE AND r.isDeleted = FALSE AND ja.candidateId = :candidateId
            """)
    Page<JobApplicationByCandidateId> getJobApplicationByCandidateId(String candidateId, Pageable pageable);

    List<JobApplication> findByJobIdAndIsDeletedFalse(String jobId);

    @Query("""
                SELECT new com.architecture.solution.repository.projection.JobApplicationByJobId(
                    ja.id, ja.jobId, ja.candidateId, ja.cvFileId, ja.status, f.originalFilename, u.displayedName, u.email
                )
                FROM JobApplication ja
                JOIN File f ON ja.cvFileId = f.id
                JOIN User u ON ja.candidateId = u.id
                WHERE ja.isDeleted = FALSE AND f.isDeleted = FALSE AND u.isDeleted = FALSE AND ja.jobId = :jobId
            """)
    Page<JobApplicationByJobId> findJobApplicationByJobId(String jobId, Pageable pageable);
}
