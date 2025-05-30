package com.icispp.notificationservice.controllers;

import com.icispp.notificationservice.models.Subscription;
import com.icispp.notificationservice.services.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @PostMapping("/sendToSubscribers")
    public void sendToSubscribers(@RequestParam String subject, @RequestParam String content, @RequestParam Subscription subscription) {
        messageService.sendMessageToSubscribers(subject, content, subscription);
    }
}
