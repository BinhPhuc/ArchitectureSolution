package com.architecture.solution.service.impl;

import com.architecture.solution.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {
    @Override
    public void sendEmail(String recipient, String subject, String body) {
        // Simulate sending email
        log.info("Sending email to: {}", recipient);
        log.info("Subject: {}", subject);
        log.info("Body: {}", body);
    }
}
