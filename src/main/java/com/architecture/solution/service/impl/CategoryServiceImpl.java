package com.architecture.solution.service.impl;

import com.architecture.solution.repository.projection.CategoryJobCount;
import com.architecture.solution.dto.category.response.CategoryResponse;
import com.architecture.solution.repository.CategoryRepository;
import com.architecture.solution.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public List<CategoryResponse> getAllCategory() {
        List<CategoryJobCount> categoryJobCountResponses = categoryRepository.findAllCategoryWithJobCount();
        List<CategoryResponse> categoryResponses = new ArrayList<>();
        for(CategoryJobCount categoryJobCount: categoryJobCountResponses){
            CategoryResponse categoryResponse = CategoryResponse.builder().id(categoryJobCount.getId())
                    .name(categoryJobCount.getName())
                    .jobCount(categoryJobCount.getJobCount())
                    .build();
            categoryResponses.add(categoryResponse);
        }
        return categoryResponses;
    }
}
