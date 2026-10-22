package com.architecture.solution.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.SimpleAsyncTaskScheduler;

@Configuration
public class SchedulerConfig {
    @Bean
    @Primary
    public TaskScheduler taskScheduler() {
        return new SimpleAsyncTaskScheduler();
    }
}
