package com.icispp.notificationservice.controllers;

import com.icispp.notificationservice.services.MessageService;
import com.icispp.notificationservice.services.SubscriptionService;
import com.icispp.notificationservice.services.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@RequestMapping("/api")
public class MainController {

    @Autowired
    private SubscriptionService subscriptionService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private UserService userService;

    @GetMapping("v1/health")
    public Map<String, String> hello(@RequestHeader(value = "Origin", required = false) String origin) {
        log.info("Received request from origin: {}", origin);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Hello from the server!");
        response.put("origin", origin != null ? origin : "unknown");

        return response;
    }
}
