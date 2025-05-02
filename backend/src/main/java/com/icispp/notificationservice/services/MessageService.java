package com.icispp.notificationservice.services;

import com.icispp.notificationservice.models.Message;
import com.icispp.notificationservice.models.Subscription;
import com.icispp.notificationservice.models.User;
import com.icispp.notificationservice.repositories.SqlMessageRepository;
import com.icispp.notificationservice.repositories.SqlSubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageService {

    private final SqlMessageRepository messageRepository;
    private final EmailServiceImpl emailService;
    private final SqlSubscriptionRepository sqlSubscriptionRepository;

    @Autowired
    public MessageService(SqlMessageRepository messageRepository, SqlSubscriptionRepository subscriptionRepository, EmailServiceImpl emailService) {
        this.messageRepository = messageRepository;
        this.emailService = emailService;
        this.sqlSubscriptionRepository = subscriptionRepository;
    }

    public Message sendMessageToUser(String subject, String content, User user, Subscription subscription) {
        Message message = Message.builder()
                .subject(subject)
                .content(content)
                .user(user)
                .subscription(subscription)
                .sentAt(LocalDateTime.now())
                .delivered(false)
                .build();

        emailService.sendSimpleMailToUser(message, user);

        return messageRepository.save(message);
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
