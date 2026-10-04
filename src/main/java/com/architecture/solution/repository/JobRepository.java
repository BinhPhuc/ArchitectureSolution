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

import java.util.Optional;

@Repository
public interface JobRepository extends JpaRepository<Job, String> {
    Optional<Job> findByIdAndIsDeletedFalse(String id);

    Page<Job> findByRecruiterIdAndIsDeletedFalse(String recruiterId, Pageable pageable);

    Page<Job> findByRecruiterIdAndStatusAndIsDeletedFalse(String recruiterId, JobStatus jobStatus, Pageable pageable);

    @Query("""
            SELECT j FROM Job j
            WHERE j.isDeleted = false
              AND (:title IS NULL OR j.title LIKE CONCAT('%', :title, '%'))
              AND (:jobType IS NULL OR j.jobType = :jobType)
              AND (:status IS NULL OR j.status = :status)
            """)
    Page<Job> search(@Param("title") String title,
                     @Param("jobType") JobType jobType,
                     @Param("status") JobStatus status,
                     Pageable pageable);

    boolean existsByIdAndStatusAndIsDeletedFalse(String id, JobStatus status);
}
