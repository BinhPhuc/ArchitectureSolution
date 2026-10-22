package com.architecture.solution.service.impl;

import com.architecture.solution.service.NotificationService;
import com.architecture.solution.service.SchedulerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class SchedulerServiceImpl implements SchedulerService {
    private final TaskScheduler taskScheduler;
    private final NotificationService notificationService;

    @Override
    public void scheduleOneTimeTask(Instant executionTime) {
        taskScheduler.schedule(() -> {
            log.info("Executing scheduled task at {}", Instant.now());
            notificationService.send("CuongTran", "FlashSale", "Flash Sale is starting now!");
        }, executionTime);
    }
}
