package com.icispp.notificationservice.kafka;

import com.icispp.notificationservice.services.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaConsumer {
    @Autowired
    private MessageService messageService;

    @KafkaListener(topics = "email-notifications-success")
    public void listen(ConsumerRecord<String, Long> record, Acknowledgment ack) {
        Long sendedId = record.value();
        log.info("Received message from Kafka:");
        messageService.markMessageAsDelivered(sendedId);
        ack.acknowledge();
    }
}