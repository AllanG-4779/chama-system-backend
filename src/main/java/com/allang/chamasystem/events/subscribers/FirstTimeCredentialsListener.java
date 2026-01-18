package com.allang.chamasystem.events.subscribers;

import com.allang.chamasystem.events.bus.UserEventBus;
import com.allang.chamasystem.notification.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FirstTimeCredentialsListener {
    public FirstTimeCredentialsListener(UserEventBus userEventBus, NotificationService notificationService) {
        userEventBus.events()
                .subscribe(event -> {
                    log.info("Received event for first time credentials setup: {}", event);
                    notificationService.sendEmail("onyangoallang@gmail.com",
                            "First Time Credentials Setup",
                            "Please set up your credentials using the following link: http://example.com/setup-credentials");

                });
    }


}
