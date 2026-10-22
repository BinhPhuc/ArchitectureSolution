package com.architecture.solution.service;

public interface EmailService {
    void sendEmail(String recipient, String subject, String body);
}
