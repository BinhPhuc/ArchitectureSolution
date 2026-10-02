package com.architecture.solution.service;

import com.architecture.solution.dto.category.response.CategoryResponse;

import java.util.List;

public interface CategoryService {
    List<CategoryResponse> getAllCategory();
}
