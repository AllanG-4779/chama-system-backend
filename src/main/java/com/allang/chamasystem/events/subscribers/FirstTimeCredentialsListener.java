package com.allang.chamasystem.events.subscribers;

import com.allang.chamasystem.events.bus.SystemEventBus;
import com.allang.chamasystem.notification.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FirstTimeCredentialsListener {
    public FirstTimeCredentialsListener(SystemEventBus userEventBus, NotificationService notificationService) {
        userEventBus.userEvents()
                .subscribe(event -> {
                    log.info("Received event for first time credentials setup: {}", event);
                    notificationService.sendEmail("onyangoallang@gmail.com",
                            "First Time Credentials Setup",
                            "Please set up your credentials using the following link: http://example.com/setup-credentials");

                });
    }



}
