package com.icispp.notificationservice.controllers;

import com.icispp.notificationservice.exception.ServerException;
import com.icispp.notificationservice.models.Subscription;
import com.icispp.notificationservice.models.User;
import com.icispp.notificationservice.services.MessageService;
import com.icispp.notificationservice.services.SubscriptionService;
import com.icispp.notificationservice.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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

    @GetMapping("/hello")
    public Map<String, String> hello(@RequestHeader(value = "Origin", required = false) String origin) {
        logger.info("Received request from origin: {}", origin);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Hello from the server!");
        response.put("origin", origin != null ? origin : "unknown");

        return response;
    }

    @PostMapping("/subscriptions/{subscriptionId}/addUser")
    public ResponseEntity<Subscription> addUserToSubscription(@PathVariable Long subscriptionId, @RequestParam Long userId) {
        if(userId == null || userId < 1) {
            throw new ServerException(HttpStatus.BAD_REQUEST, "Некоректный id пользователя");
        }
        if(subscriptionId == null || subscriptionId < 1) {
            throw new ServerException(HttpStatus.BAD_REQUEST, "Некоректный id рассылки");
        }

        Optional<Subscription> subscriptionOptional = subscriptionService.findById(subscriptionId);
        if(subscriptionOptional.isEmpty()) {
            throw new ServerException(HttpStatus.NOT_FOUND, "Подписка не найдена");
        }
        Optional<User> userOptional = userService.findById(userId);
        if(userOptional.isEmpty()) {
            throw new ServerException(HttpStatus.NOT_FOUND, "Пользователь не найден");
        }
        Subscription subscription = subscriptionService.addUserToSubscription(userOptional.get(), subscriptionOptional.get());
        return ResponseEntity.ok(subscription);
    }

    @PostMapping("/subscriptions/{subscriptionId}/sendMessage")
    public ResponseEntity<String> sendMessageToSubscribers(
            @PathVariable Long subscriptionId,
            @RequestParam String subject,
            @RequestParam String content) {
        if(subscriptionId == null || subscriptionId < 1) {
            throw new ServerException(HttpStatus.BAD_REQUEST, "Некоректный id рассылки");
        }
        if(subject == null || subject.isEmpty()) {
            throw new ServerException(HttpStatus.BAD_REQUEST, "Нельзя отправить сообщение без темы");
        }
        if(content == null || content.isEmpty()) {
            throw new ServerException(HttpStatus.BAD_REQUEST, "Нельзя отправить пустое сообщение");
        }
        Optional<Subscription> subscriptionOptional = subscriptionService.findById(subscriptionId);
        if(subscriptionOptional.isEmpty()) {
            throw new ServerException(HttpStatus.NOT_FOUND, "Такой рассылки не существует");
        }
        messageService.sendMessageToSubscribers(subject, content, subscriptionOptional.get());

        return ResponseEntity.ok("Сообщение успешно отправлено");
    }
}
