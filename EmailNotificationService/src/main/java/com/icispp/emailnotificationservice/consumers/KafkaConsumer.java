package com.icispp.emailnotificationservice.consumers;

import com.icispp.emailnotificationservice.dto.SendEmailMessage;
import com.icispp.emailnotificationservice.services.EmailSenderService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaConsumer {

    @Autowired
    private EmailSenderService emailSenderService;

    @KafkaListener(topics = "email-notifications", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(SendEmailMessage emailDetails) {
        log.info("Received message from Kafka:");

        boolean sent = emailSenderService.sendEmail(emailDetails);

        if (sent) {
            log.info("Email notification processed successfully from: {}", emailDetails.getFrom());
        } else {
            log.error("Failed to send email notification from: {}", emailDetails.getFrom());
        }
    }
}
