package com.architecture.solution.repository;

import com.architecture.solution.repository.projection.CategoryJobCount;
import com.architecture.solution.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {
    @Query("""
            SELECT new com.architecture.solution.repository.projection.CategoryJobCount(
                        c.id, c.name, COUNT(j.id))
            FROM Category c
            LEFT JOIN JobCategory jc ON jc.categoryId = c.id AND jc.isDeleted = false
            LEFT JOIN Job j ON j.id = jc.jobId
                           AND j.isDeleted = false
                           AND j.status = com.architecture.solution.enums.JobStatus.OPEN
            WHERE c.isDeleted = false
            GROUP BY c.id, c.name
            ORDER BY c.name
            """)
    List<CategoryJobCount> findAllCategoryWithJobCount();
}
