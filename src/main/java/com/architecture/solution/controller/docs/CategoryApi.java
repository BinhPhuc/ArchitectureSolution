package com.architecture.solution.controller.docs;

import com.architecture.solution.dto.category.response.CategoryResponse;
import com.architecture.solution.dto.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "Category Controller")
public interface CategoryApi {

    @Operation(
            summary = "Get all categories",
            description = "Retrieve all categories with the number of jobs in each category"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Category list retrieved"
    )
    ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategory();
}
