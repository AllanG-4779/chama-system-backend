package com.allang.chamasystem.notification;

import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    public void sendSms(String phoneNumber, String message) {
        // Integrate with an SMS gateway API to send SMS
        System.out.println("Sending SMS to " + phoneNumber + ": " + message);
    }

    public void sendEmail(String email, String subject, String body) {
        // Integrate with an Email service API to send emails
        System.out.println("Sending Email to " + email + ": " + subject + " - " + body);
    }
}
