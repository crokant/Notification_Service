package com.icispp.emailnotificationservice.producer;

import com.icispp.emailnotificationservice.services.EmailSenderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaProducer {

    @Autowired
    private KafkaTemplate<String, Long> kafkaTemplate;

    @Value("email-notifications-success")
    private String topicName;

    public void produceSendedMassageId(Long id ) {
        log.info("Sending message to Kafka: {}", id);
        kafkaTemplate.send(topicName, id);
    }
}
