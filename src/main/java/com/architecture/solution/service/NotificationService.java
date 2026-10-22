package com.architecture.solution.service;

public interface NotificationService {
    void send(String recipient, String subject, String message);
}
