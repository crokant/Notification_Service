package com.icispp.notificationservice.controllers;

import com.icispp.notificationservice.services.MessageService;
import com.icispp.notificationservice.services.SubscriptionService;
import com.icispp.notificationservice.services.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "Home API", description = "Just telemetry")
@RestController
@RequestMapping("/api")
public class MainController {

    private final SubscriptionService subscriptionService;
    private final MessageService messageService;
    private final UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    @Autowired
    public MainController(SubscriptionService subscriptionService, MessageService messageService, UserService userService) {
        this.subscriptionService = subscriptionService;
        this.messageService = messageService;
        this.userService = userService;
    }

    @GetMapping("v1/health")
    public Map<String, String> hello(@RequestHeader(value = "Origin", required = false) String origin) {
        logger.info("Received request from origin: {}", origin);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Hello from the server!");
        response.put("origin", origin != null ? origin : "unknown");

        return response;
    }
}
