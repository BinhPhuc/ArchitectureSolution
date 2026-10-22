package com.architecture.solution.controller;

import com.architecture.solution.dto.common.ApiResponse;
import com.architecture.solution.service.SchedulerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/test")
public class TestController {
    private final SchedulerService schedulerService;

    @GetMapping("/scheduler")
    public ResponseEntity<ApiResponse<String>> scheduleTask() {
        schedulerService.scheduleOneTimeTask(java.time.Instant.now().plusSeconds(10));
        return ResponseEntity.ok(ApiResponse.created("Task scheduled successfully"));
    }
}
