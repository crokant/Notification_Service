package com.icispp.notificationservice.services;

import com.icispp.notificationservice.models.Subscription;
import com.icispp.notificationservice.models.User;
import com.icispp.notificationservice.repositories.SqlSubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
/**
 * Сервисный класс для управления подписками в сервисе уведомлений.
 * Этот класс предоставляет методы для создания подписок, добавления пользователей в подписки
 * и поиска подписок по их идентификатору.
 *
 * <p>
 * {@link SubscriptionService} взаимодействует с {@link SqlSubscriptionRepository}
 * для выполнения операций CRUD над сущностями подписок.
 * </p>
 *
 * <p>
 * Этот сервис аннотирован {@link Service}, что указывает на то, что он является компонентом
 * сервиса Spring.
 * </p>
 */
@Service
public class SubscriptionService {
    @Autowired
    private SqlSubscriptionRepository subscriptionRepository;

    /**
     * Находит подписку по ее идентификатору.
     *
     * @param id идентификатор подписки для поиска
     * @return Optional, содержащий найденную подписку, или пустой Optional, если подписка не найдена
     */
    public Optional<Subscription> findById(Long id) {
        return subscriptionRepository.findSubscriptionById(id);
    }

    /**
     * Добавляет пользователя в список подписчиков подписки.
     *
     * @param user пользователь, который будет добавлен в подписку
     * @param subscription подписка, в которую будет добавлен пользователь
     * @return обновленная подписка после добавления пользователя
     */
    public Subscription addUserToSubscription(User user, Subscription subscription) {
        subscriptionRepository.addSubscriptionToUser(user.getId(), subscription.getId());
        if (subscription.getSubscribers() == null) {
            subscription.setSubscribers(new HashSet<>());
        }
        subscription.getSubscribers().add(user);
        return subscription;
    }

    /**
     * Создает новую подписку с указанным именем и создателем.
     *
     * @param name имя новой подписки
     * @param creator пользователь, который создает подписку
     * @return созданная подписка
     */
    public Subscription createSubscription(String name, User creator) {
        Subscription subscription = Subscription.builder()
                .name(name)
                .creator(creator)
                .build();
        return subscriptionRepository.createSubscription(creator.getId(), subscription);
    }
}
