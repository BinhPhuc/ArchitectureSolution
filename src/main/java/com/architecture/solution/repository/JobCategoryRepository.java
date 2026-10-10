package com.architecture.solution.repository;

import com.architecture.solution.entity.JobCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobCategoryRepository extends JpaRepository<JobCategory, String> {
    List<JobCategory> findByJobIdAndIsDeletedFalse(String jobId);
}
