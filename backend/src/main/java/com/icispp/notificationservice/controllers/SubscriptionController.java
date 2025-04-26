package com.icispp.notificationservice.controllers;

import com.icispp.notificationservice.exception.ServerException;
import com.icispp.notificationservice.models.Subscription;
import com.icispp.notificationservice.models.User;
import com.icispp.notificationservice.services.MessageService;
import com.icispp.notificationservice.services.SubscriptionService;
import com.icispp.notificationservice.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@Tag(name = "Subs Api", description = "All what you need for working  with subscriptions")
@RestController
@RequestMapping("/api/")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final UserService userService;
    private final MessageService messageService;

    @Autowired
    public SubscriptionController(SubscriptionService subscriptionService,
                                  UserService userService,
                                  MessageService messageService) {
        this.subscriptionService = subscriptionService;
        this.userService = userService;
        this.messageService = messageService;
    }

    @Operation(summary = "Create new subscription")
    @PostMapping("v1/subscriptions/create")
    public Subscription createSubscription(@RequestParam String name, @RequestParam User creator) {
        return subscriptionService.createSubscription(name, creator);
    }

    @Operation(summary = "Add new user into subscription")
    @PostMapping("v1/subscriptions/addUser")
    public Subscription addUserToSubscription(@RequestParam User user, @RequestParam Subscription subscription) {
        return subscriptionService.addUserToSubscription(user, subscription);
    }

    @PostMapping("v1/subscriptions/{subscriptionId}/addUser")
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
    @PostMapping("v1/subscriptions/{subscriptionId}/sendMessage")
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
