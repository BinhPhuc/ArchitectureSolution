package com.architecture.solution.repository;

import com.architecture.solution.entity.Job;
import com.architecture.solution.enums.JobStatus;
import com.architecture.solution.enums.JobType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRepository extends JpaRepository<Job, String> {
    Page<Job> findByRecruiterId(String recruiterId, Pageable pageable);

    @Query("""
            SELECT j FROM Job j
            WHERE (:title IS NULL OR j.title LIKE CONCAT('%', :title, '%'))
              AND (:jobType IS NULL OR j.jobType = :jobType)
              AND (:status IS NULL OR j.status = :status)
            """)
    Page<Job> search(@Param("title") String title,
                     @Param("jobType") JobType jobType,
                     @Param("status") JobStatus status,
                     Pageable pageable);
}
