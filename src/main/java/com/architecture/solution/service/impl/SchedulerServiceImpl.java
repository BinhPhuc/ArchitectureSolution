package com.architecture.solution.service.impl;

import com.architecture.solution.entity.User;
import com.architecture.solution.repository.UserRepository;
import com.architecture.solution.service.NotificationService;
import com.architecture.solution.service.SchedulerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SchedulerServiceImpl implements SchedulerService {
    private final TaskScheduler taskScheduler;
    private final NotificationService notificationService;
    private final UserRepository userRepository;

    @Override
    public void scheduleOneTimeTask(Instant executionTime) {
        taskScheduler.schedule(() -> {
            List<User> users = userRepository.findAll();
            log.info("Executing scheduled task at {}", Instant.now());
            users.forEach(user -> notificationService.send(user.getUsername(), "FlashSale", "Flash Sale is starting now!"));
        }, executionTime);
    }
}
