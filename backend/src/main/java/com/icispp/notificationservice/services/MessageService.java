package com.icispp.notificationservice.services;

import com.icispp.notificationservice.dto.SendEmailMessage;
import com.icispp.notificationservice.kafka.KafkaProducer;
import com.icispp.notificationservice.models.Message;
import com.icispp.notificationservice.models.Subscription;
import com.icispp.notificationservice.models.User;
import com.icispp.notificationservice.repositories.SqlMessageRepository;
import com.icispp.notificationservice.repositories.SqlSubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageService {

    @Autowired
    private KafkaProducer kafkaProducer;
    @Autowired
    private SqlMessageRepository messageRepository;

    public Message sendMessageToUser(String subject, String content, User user, Subscription subscription) {

        SendEmailMessage sendEmailMessage = generateValuableKafkaMessage(subject, content, user, subscription);
        kafkaProducer.sendEmailMessage(sendEmailMessage);

        Message message = Message.builder()
                .subject(subject)
                .content(content)
                .user(user)
                .subscription(subscription)
                .sentAt(LocalDateTime.now())
                .delivered(false)
                .build();

        return messageRepository.save(message);
    }

    public Message sendMessageToUser(Message message) {

        SendEmailMessage sendEmailMessage = generateValuableKafkaMessage(message.getSubject(), message.getContent(), message.getUser(), message.getSubscription());
        kafkaProducer.sendEmailMessage(sendEmailMessage);

        return messageRepository.save(message);
    }

    SendEmailMessage generateValuableKafkaMessage(String subject, String content, User user, Subscription subscription) {
        return SendEmailMessage
                .builder()
                .subject(subject)
                .from(subscription.getCreator().getName())
                .to(subscription.getSubscribers().stream().map(User::getName).collect(Collectors.toSet()))
                .build();
    }

    public void sendMessageToSubscribers(String subject, String content, Subscription subscription) {
        subscription.getSubscribers().forEach(user -> sendMessageToUser(subject, content, user, subscription));
    }

    /**
     * Получение всех сообщений для конкретного пользователя
     */
    public List<Message> getMessagesForUser(User user) {
        return messageRepository.findAllByUser(user);
    }

    /**
     * Получение сообщений по подписке
     */
    public List<Message> getMessagesForSubscription(Subscription subscription) {
        return messageRepository.findAllBySubscription(subscription);
    }

    /**
     * Обновление статуса доставки сообщения
     */
    public void markMessageAsDelivered(Long messageId) {
        messageRepository.updateDeliveryStatus(messageId, true);
    }

    /**
     * Получение недоставленных сообщений
     */
    public List<Message> getUndeliveredMessages() {
        return messageRepository.findByDeliveredFalse();
    }

    /**
     * Удаление сообщения по ID
     */
    public void deleteMessage(Long messageId) {
        messageRepository.deleteById(messageId);
    }

}
