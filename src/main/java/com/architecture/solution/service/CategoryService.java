package com.architecture.solution.service;

import com.architecture.solution.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {
    List<CategoryResponse> showAllCategory();
    List<CategoryResponse> getAllCategory();
}
