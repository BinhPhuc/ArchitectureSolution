package com.architecture.solution.service.impl;

import com.architecture.solution.service.EmailService;
import com.architecture.solution.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {
    private final EmailService emailService;

    @Override
    public void send(String recipient, String subject, String message) {
        emailService.sendEmail(recipient, subject, message);
    }
}
