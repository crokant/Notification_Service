package com.icispp.emailnotificationservice.consumers;

import com.icispp.emailnotificationservice.dto.SendEmailMessage;
import com.icispp.emailnotificationservice.services.EmailSenderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaConsumer {

    @Autowired
    private EmailSenderService emailSenderService;

    @KafkaListener(topics = "email-notifications", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(ConsumerRecord<String, SendEmailMessage> record, Acknowledgment ack) {
        SendEmailMessage message = record.value();
        log.info("Received message from Kafka:");
        boolean sent = emailSenderService.sendEmail(message);
        ack.acknowledge();
        if (sent) {
            log.info("Email notification processed successfully from: {}", message.getFrom());
        } else {
            log.error("Failed to send email notification from: {}", message.getFrom());
        }
    }
}
