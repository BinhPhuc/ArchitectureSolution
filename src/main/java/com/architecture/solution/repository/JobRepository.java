package com.architecture.solution.repository;

import com.architecture.solution.entity.Job;
import com.architecture.solution.enums.JobStatus;
import com.architecture.solution.enums.JobType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRepository extends JpaRepository<Job, String> {
    Page<Job> findByTitleContaining(String title, Pageable pageable);
    Page<Job> findByRecruiterId(String recruiterId, Pageable pageable);
    Page<Job> findByJobType(JobType jobType, Pageable pageable);
    Page<Job> findByStatus(JobStatus status, Pageable pageable);
}
