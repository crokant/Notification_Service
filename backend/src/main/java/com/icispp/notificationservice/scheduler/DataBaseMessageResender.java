package com.icispp.notificationservice.scheduler;

import com.icispp.notificationservice.models.Message;
import com.icispp.notificationservice.services.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataBaseMessageResender {
    @Autowired
    private MessageService messageService;

    @Scheduled(fixedRateString = "600s")
    public void resend(){
        List<Message> undeliveredMessages = messageService.getUndeliveredMessages();
        for (Message message : undeliveredMessages) {
            messageService.sendMessageToUser(message);
        }
    }
}
