package com.icispp.notificationservice.kafka;


import com.icispp.notificationservice.dto.SendEmailMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaProducer {

    @Autowired
    private KafkaTemplate<String, SendEmailMessage> kafkaTemplate;

    @Value("${spring.kafka.topic.email-notifications}")
    private String topicName;

    public void sendEmailMessage(SendEmailMessage message) {
        log.info("Sending message to Kafka: {}", message);
        kafkaTemplate.send(topicName, message);
    }
}
