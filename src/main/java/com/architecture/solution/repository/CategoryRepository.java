package com.architecture.solution.repository;

import com.architecture.solution.dto.response.CategoryJobCountResponse;
import com.architecture.solution.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {
    List<Category> findAll();

    @Query(value = """
            SELECT
                cgr.id,
                cgr.name,
                COUNT(jcgr.job_id) AS job_count
            FROM categories cgr
            LEFT JOIN job_categories jcgr
                ON cgr.id = jcgr.category_id
            GROUP BY
                cgr.id,
                cgr.name
            ORDER BY job_count DESC;""" , nativeQuery = true)
    List<CategoryJobCountResponse> findAllCategoryJobCountResponse();
}
