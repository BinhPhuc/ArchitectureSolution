package com.architecture.solution.service.impl;

import com.architecture.solution.dto.response.CategoryJobCountResponse;
import com.architecture.solution.dto.response.CategoryResponse;
import com.architecture.solution.entity.Category;
import com.architecture.solution.entity.JobApplication;
import com.architecture.solution.entity.JobCategory;
import com.architecture.solution.repository.CategoryRepository;
import com.architecture.solution.repository.JobApplicationRepository;
import com.architecture.solution.repository.JobCategoryRepository;
import com.architecture.solution.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final JobCategoryRepository jobCategoryRepository;
    @Override
    public List<CategoryResponse> showAllCategory() {
        List<Category> listCategory= categoryRepository.findAll();
        Map<Category, Integer> categoryToNumberJob = new HashMap<Category, Integer>();
        for(Category cgr: listCategory) {
            String cgrId = cgr.getId();
            List<JobCategory> jobByIdCategory = jobCategoryRepository.findByCategoryId(cgrId);
            int numJob = jobByIdCategory.size();
            categoryToNumberJob.put(cgr, numJob);
        }
        List<CategoryResponse> categoryResponses = new ArrayList<>();
        for(Category cgr: listCategory) {
            CategoryResponse categoryResponse = CategoryResponse.builder().id(cgr.getId())
                    .name(cgr.getName())
                    .count(categoryToNumberJob.get(cgr)).build();
            categoryResponses.add(categoryResponse);
        }
        return categoryResponses;
    }
    public List<CategoryResponse> getAllCategory() {
        List<CategoryJobCountResponse> categoryJobCountResponses = categoryRepository.findAllCategoryJobCountResponse();
        List<CategoryResponse> categoryResponses = new ArrayList<>();
        for(CategoryJobCountResponse cgrJobCount: categoryJobCountResponses){
            CategoryResponse cgrRes = CategoryResponse.builder().id(cgrJobCount.getId())
                    .name(cgrJobCount.getName())
                    .count(cgrJobCount.getCount().intValue())
                    .build();
            categoryResponses.add(cgrRes);
        }
        return categoryResponses;
    }
}
