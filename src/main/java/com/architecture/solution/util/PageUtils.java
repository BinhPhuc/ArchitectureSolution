package com.architecture.solution.util;

import com.architecture.solution.dto.jobapplication.CandidateJobApplicationSummaryResponse;
import com.architecture.solution.dto.common.PageResponse;
import com.architecture.solution.dto.job.response.GetJobResponse;
import com.architecture.solution.dto.jobapplication.RecruiterJobApplicationSummaryResponse;
import com.architecture.solution.entity.Job;
import com.architecture.solution.exception.InvalidArgumentException;
import com.architecture.solution.repository.projection.CandidateJobApplicationSummary;
import com.architecture.solution.repository.projection.RecruiterJobApplicationSummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

public class PageUtils {
    private PageUtils() {
    }

    public static PageResponse<List<GetJobResponse>> mapPageJobToPageResponse(Page<Job> pageJob) {
        Page<GetJobResponse> pageJobResponse = pageJob.map(job ->
                GetJobResponse.builder()
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
        List<GetJobResponse> content = pageJobResponse.getContent();
        return PageResponse.
                <List<GetJobResponse>>builder()
                .totalPage(totalPages)
                .pageNum(pageNum)
                .pageSize(pageSize)
                .items(content)
                .build();
    }

    public static PageResponse<List<CandidateJobApplicationSummaryResponse>> mapPageCandidateJobApplicationSummaryToPageResponse(Page<CandidateJobApplicationSummary> pageApplication) {
        Page<CandidateJobApplicationSummaryResponse> pageApplicationResponse = pageApplication.map(application ->
                CandidateJobApplicationSummaryResponse
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
        List<CandidateJobApplicationSummaryResponse> content = pageApplicationResponse.getContent();
        return PageResponse.
                <List<CandidateJobApplicationSummaryResponse>>builder()
                .totalPage(totalPages)
                .pageNum(pageNum)
                .pageSize(pageSize)
                .items(content)
                .build();
    }

    public static PageResponse<List<RecruiterJobApplicationSummaryResponse>> mapPageRecruiterJobApplicationSummaryToPageResponse(Page<RecruiterJobApplicationSummary> pageJobApplication) {
        Page<RecruiterJobApplicationSummaryResponse> pageJobApplicationResponse =
                pageJobApplication.map(application ->
                        RecruiterJobApplicationSummaryResponse
                                .builder()
                                .id(application.getId())
                                .jobId(application.getJobId())
                                .candidateId(application.getCandidateId())
                                .cvFileId(application.getCvFileId())
                                .status(application.getStatus())
                                .originalFileName(application.getOriginalFileName())
                                .candidateDisplayedName(application.getCandidateDisplayedName())
                                .email(application.getEmail())
                                .build()
                );
        int totalPages = pageJobApplicationResponse.getTotalPages();
        int pageNum = pageJobApplicationResponse.getNumber() + 1;
        int pageSize = pageJobApplicationResponse.getSize();
        List<RecruiterJobApplicationSummaryResponse> content =
                pageJobApplicationResponse.getContent();
        return PageResponse.
                <List<RecruiterJobApplicationSummaryResponse>>builder()
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
