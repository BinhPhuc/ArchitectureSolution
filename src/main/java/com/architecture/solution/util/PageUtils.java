package com.architecture.solution.util;

import com.architecture.solution.dto.candidate.response.GetApplicationResponse;
import com.architecture.solution.dto.common.PageResponse;
import com.architecture.solution.dto.job.response.SearchJobResponse;
import com.architecture.solution.entity.Job;
import com.architecture.solution.exception.InvalidArgumentException;
import com.architecture.solution.repository.projection.CandidateJobApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

public class PageUtils {
    private PageUtils() {
    }

    public static PageResponse<List<SearchJobResponse>> mapPageJobToPageResponse(Page<Job> pageJob) {
        Page<SearchJobResponse> pageJobResponse = pageJob.map(job ->
                SearchJobResponse.builder()
                        .jobType(job.getJobType())
                        .id(job.getId())
                        .description(job.getDescription())
                        .title(job.getTitle())
                        .status(job.getStatus())
                        .salaryMin(job.getSalaryMin())
                        .salaryMax(job.getSalaryMax())
                        .recruiterId(job.getRecruiterId())
                        .createdAt(job.getCreatedAt())
                        .build()
        );
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

    public static PageResponse<List<GetApplicationResponse>> mapPageApplicationToPageResponse(Page<CandidateJobApplication> pageApplication) {
        Page<GetApplicationResponse> pageApplicationResponse = pageApplication.map(application ->
            GetApplicationResponse
                    .builder()
                    .candidateId(application.getCandidateId())
                    .jobId(application.getJobId())
                    .title(application.getTitle())
                    .description(application.getDescription())
                    .companyName(application.getCompanyName())
                    .jobType(application.getJobType())
                    .status(application.getStatus())
                    .build()
        );
        int totalPages = pageApplicationResponse.getTotalPages();
        int pageNum = pageApplicationResponse.getNumber() + 1;
        int pageSize = pageApplicationResponse.getSize();
        List<GetApplicationResponse> content = pageApplicationResponse.getContent();
        return PageResponse.
                <List<GetApplicationResponse>>builder()
                .totalPage(totalPages)
                .pageNum(pageNum)
                .pageSize(pageSize)
                .items(content)
                .build();
    }

    public static Pageable getDefaultPageable(int page, int size) {
        if (page < 1) {
            throw new InvalidArgumentException("Page must be greater than or equal to 1");
        }
        if (size < 1) {
            throw new InvalidArgumentException("Size must be greater than or equal to 1");
        }
        return PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
    }
}
