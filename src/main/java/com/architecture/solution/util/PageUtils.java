package com.architecture.solution.util;

import com.architecture.solution.dto.common.PageResponse;
import com.architecture.solution.dto.job.response.SearchJobResponse;
import com.architecture.solution.entity.Job;
import com.architecture.solution.exception.InvalidArgumentException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

public class PageUtils {
    private PageUtils() {
    }

    public static PageResponse<List<SearchJobResponse>> mapPageJobToPageResponse(Page<Job> pageJob) {
        Page<SearchJobResponse> pageJobResponse = pageJob.map(job -> {
            SearchJobResponse jobResponse = new SearchJobResponse();
            jobResponse.setJobType(job.getJobType());
            jobResponse.setId(job.getId());
            jobResponse.setDescription(job.getDescription());
            jobResponse.setTitle(job.getTitle());
            jobResponse.setStatus(job.getStatus());
            jobResponse.setSalaryMin(job.getSalaryMin());
            jobResponse.setSalaryMax(job.getSalaryMax());
            jobResponse.setRecruiterId(job.getRecruiterId());
            jobResponse.setCreatedAt(job.getCreatedAt());
            return jobResponse;
        });
        int totalPages = pageJobResponse.getTotalPages();
        int pageNum = pageJobResponse.getNumber() + 1;
        int pageSize = pageJobResponse.getSize();
        List<SearchJobResponse> content = pageJobResponse.getContent();
        return PageResponse.
                <List<SearchJobResponse>>builder()
                .totalPage(totalPages)
                .pageNum(pageNum)
                .pageSize(pageSize)
                .items(content)
                .build();
    }

    public static Pageable getDefaultPageable(int size, int page) {
        if (page < 1) {
            throw new InvalidArgumentException("Page must be greater than or equal to 1");
        }
        if (size < 1) {
            throw new InvalidArgumentException("Size must be greater than or equal to 1");
        }
        return PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
    }
}
