package com.architecture.solution.service;

import java.time.Instant;

public interface SchedulerService {
    void scheduleOneTimeTask(Instant executionTime);
}
