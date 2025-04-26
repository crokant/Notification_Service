package com.icispp.notificationservice.controllers;

import com.icispp.notificationservice.models.Subscription;
import com.icispp.notificationservice.services.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/messages")
public class MessageController {

    private final MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping("/sendToSubscribers")
    public void sendToSubscribers(@RequestParam String subject, @RequestParam String content, @RequestParam Subscription subscription) {
        messageService.sendMessageToSubscribers(subject, content, subscription);
    }
}
