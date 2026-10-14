package com.architecture.solution.controller;

import com.architecture.solution.controller.docs.CategoryApi;
import com.architecture.solution.dto.category.response.CategoryResponse;
import com.architecture.solution.dto.common.ApiResponse;
import com.architecture.solution.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/categories")
public class CategoryController implements CategoryApi {
    private final CategoryService categoryService;

    @Override
    @GetMapping("")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategory() {
        List<CategoryResponse> categoryResponses = categoryService.getAllCategory();
        return ResponseEntity.ok(ApiResponse.success(categoryResponses, "Categories retrieved successfully"));
    }
}
