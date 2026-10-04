package com.architecture.solution.repository;

import com.architecture.solution.entity.JobApplication;
import com.architecture.solution.repository.projection.CandidateJobApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, String> {
    boolean existsByJobIdAndCandidateId(String jobId, String candidateId);

    @Query("""
                SELECT new com.architecture.solution.repository.projection.CandidateJobApplication(
                    ja.candidateId, ja.jobId, j.title, j.description, r.companyName, j.jobType, ja.status
                )
                FROM JobApplication ja
                JOIN Job j on j.id = ja.jobId
                JOIN Recruiter r on j.recruiterId = r.userId
                where ja.isDeleted = false and j.isDeleted = false and r.isDeleted = false and ja.candidateId = :id
            """)
    Page<CandidateJobApplication> getJobApplicationById(String id, Pageable pageable);
}
