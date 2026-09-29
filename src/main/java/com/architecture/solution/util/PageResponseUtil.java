package com.architecture.solution.util;

import com.architecture.solution.dto.common.PageResponse;
import com.architecture.solution.dto.response.JobResponse;
import com.architecture.solution.entity.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

public class PageResponseUtil {
    private PageResponseUtil() {
    }

    public static PageResponse<List<JobResponse>> mapPageJobToPageResponse(Page<Job> pageJob) {
        Page<JobResponse> pageJobResponse = pageJob.map(job -> {
            JobResponse jobResponse = new JobResponse();
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
        int pageNum = pageJobResponse.getNumber();
        int pageSize = pageJobResponse.getSize();
        List<JobResponse> content = pageJobResponse.getContent();
        return PageResponse.
                <List<JobResponse>>builder()
                .totalPage(totalPages)
                .pageNum(pageNum)
                .pageSize(pageSize)
                .items(content)
                .build();
    }

    public static Pageable getDefaultPageable(int size, int page) {
        return PageRequest.of(page, size, Sort.by("createdAt").descending());
    }
}
