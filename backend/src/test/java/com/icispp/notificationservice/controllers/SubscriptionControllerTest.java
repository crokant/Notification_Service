package com.icispp.notificationservice.controllers;

import com.icispp.notificationservice.exception.ServerException;
import com.icispp.notificationservice.models.Subscription;
import com.icispp.notificationservice.models.User;
import com.icispp.notificationservice.services.MessageService;
import com.icispp.notificationservice.services.SubscriptionService;
import com.icispp.notificationservice.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionControllerTest {

    @InjectMocks
    private SubscriptionController subscriptionController;

    @Mock
    private SubscriptionService subscriptionService;

    @Mock
    private MessageService messageService;

    @Mock
    private UserService userService;

    @Nested
    @DisplayName("Tests for addUserToSubscription endpoint")
    class AddUserToSubscriptionTests {

        private final Long validSubscriptionId = 1L;
        private final Long validUserId = 2L;
        private Subscription subscription;
        private User user;

        @BeforeEach
        void setupNested() {
            subscription = new Subscription();
            subscription.setId(validSubscriptionId);
            user = new User();
            user.setId(validUserId);
        }

        @Test
        @DisplayName("Should add user to subscription successfully")
        void testAddUserToSubscription_Success() {
            when(subscriptionService.findById(validSubscriptionId)).thenReturn(Optional.of(subscription));
            when(userService.findById(validUserId)).thenReturn(Optional.of(user));
            when(subscriptionService.addUserToSubscription(user, subscription)).thenReturn(subscription);

            ResponseEntity<Subscription> response = subscriptionController.addUserToSubscription(validSubscriptionId, validUserId);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(subscription, response.getBody());
            verify(subscriptionService).findById(validSubscriptionId);
            verify(userService).findById(validUserId);
            verify(subscriptionService).addUserToSubscription(user, subscription);
        }

        @Test
        @DisplayName("Should throw ServerException BAD_REQUEST for invalid subscription ID (null)")
        void testAddUserToSubscription_InvalidSubscriptionId_Null() {
            Long invalidSubscriptionId = null;

            ServerException exception = assertThrows(ServerException.class, () -> {
                subscriptionController.addUserToSubscription(invalidSubscriptionId, validUserId);
            });
            assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
            assertEquals("Некоректный id рассылки", exception.getDescription());
            verifyNoInteractions(subscriptionService, userService, messageService);
        }

        @Test
        @DisplayName("Should throw ServerException BAD_REQUEST for invalid subscription ID (zero)")
        void testAddUserToSubscription_InvalidSubscriptionId_Zero() {
            Long invalidSubscriptionId = 0L;

            ServerException exception = assertThrows(ServerException.class, () -> {
                subscriptionController.addUserToSubscription(invalidSubscriptionId, validUserId);
            });
            assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
            assertEquals("Некоректный id рассылки", exception.getDescription());
            verifyNoInteractions(subscriptionService, userService, messageService);
        }

        @Test
        @DisplayName("Should throw ServerException BAD_REQUEST for invalid user ID (null)")
        void testAddUserToSubscription_InvalidUserId_Null() {
            Long invalidUserId = null;

            ServerException exception = assertThrows(ServerException.class, () -> {
                subscriptionController.addUserToSubscription(validSubscriptionId, invalidUserId);
            });
            assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
            assertEquals("Некоректный id пользователя", exception.getDescription());
            verifyNoInteractions(subscriptionService, userService, messageService);
        }

        @Test
        @DisplayName("Should throw ServerException BAD_REQUEST for invalid user ID (zero)")
        void testAddUserToSubscription_InvalidUserId_Zero() {
            Long invalidUserId = 0L;

            ServerException exception = assertThrows(ServerException.class, () -> {
                subscriptionController.addUserToSubscription(validSubscriptionId, invalidUserId);
            });
            assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
            assertEquals("Некоректный id пользователя", exception.getDescription());
            verifyNoInteractions(subscriptionService, userService, messageService);
        }


        @Test
        @DisplayName("Should throw ServerException NOT_FOUND when subscription is not found")
        void testAddUserToSubscription_SubscriptionNotFound() {
            when(subscriptionService.findById(validSubscriptionId)).thenReturn(Optional.empty());

            ServerException exception = assertThrows(ServerException.class, () -> {
                subscriptionController.addUserToSubscription(validSubscriptionId, validUserId);
            });

            assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
            assertEquals("Подписка не найдена", exception.getDescription());
            verify(subscriptionService).findById(validSubscriptionId);
            verify(userService, never()).findById(anyLong());
            verify(subscriptionService, never()).addUserToSubscription(any(), any());
        }

        @Test
        @DisplayName("Should throw ServerException NOT_FOUND when user is not found")
        void testAddUserToSubscription_UserNotFound() {
            when(subscriptionService.findById(validSubscriptionId)).thenReturn(Optional.of(subscription));
            when(userService.findById(validUserId)).thenReturn(Optional.empty());

            ServerException exception = assertThrows(ServerException.class, () -> {
                subscriptionController.addUserToSubscription(validSubscriptionId, validUserId);
            });

            assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
            assertEquals("Пользователь не найден", exception.getDescription());
            verify(subscriptionService).findById(validSubscriptionId);
            verify(userService).findById(validUserId);
            verify(subscriptionService, never()).addUserToSubscription(any(), any());
        }
    }


    @Nested
    @DisplayName("Tests for sendMessageToSubscribers endpoint")
    class SendMessageToSubscribersTests {

        private final Long validSubscriptionId = 1L;
        private final String validSubject = "Test Subject";
        private final String validContent = "Test Content";
        private Subscription subscription;

        @BeforeEach
        void setupNested() {
            subscription = new Subscription();
            subscription.setId(validSubscriptionId);
        }

        @Test
        @DisplayName("Should send message successfully")
        void testSendMessageToSubscribers_Success() {
            when(subscriptionService.findById(validSubscriptionId)).thenReturn(Optional.of(subscription));

            ResponseEntity<String> response = subscriptionController.sendMessageToSubscribers(validSubscriptionId, validSubject, validContent);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("Сообщение успешно отправлено", response.getBody());
            verify(subscriptionService).findById(validSubscriptionId);
            verify(messageService).sendMessageToSubscribers(validSubject, validContent, subscription);
        }

        @Test
        @DisplayName("Should throw ServerException BAD_REQUEST for invalid subscription ID (null)")
        void testSendMessageToSubscribers_InvalidSubscriptionId_Null() {
            Long invalidSubscriptionId = null;

            ServerException exception = assertThrows(ServerException.class, () -> {
                subscriptionController.sendMessageToSubscribers(invalidSubscriptionId, validSubject, validContent);
            });
            assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
            assertEquals("Некоректный id рассылки", exception.getDescription());
            verifyNoInteractions(subscriptionService, userService, messageService);
        }

        @Test
        @DisplayName("Should throw ServerException BAD_REQUEST for invalid subscription ID (negative)")
        void testSendMessageToSubscribers_InvalidSubscriptionId_Negative() {
            Long invalidSubscriptionId = -5L;

            ServerException exception = assertThrows(ServerException.class, () -> {
                subscriptionController.sendMessageToSubscribers(invalidSubscriptionId, validSubject, validContent);
            });
            assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
            assertEquals("Некоректный id рассылки", exception.getDescription());
            verifyNoInteractions(subscriptionService, userService, messageService);
        }

        @Test
        @DisplayName("Should throw ServerException BAD_REQUEST for null subject")
        void testSendMessageToSubscribers_NullSubject() {
            String invalidSubject = null;

            ServerException exception = assertThrows(ServerException.class, () -> {
                subscriptionController.sendMessageToSubscribers(validSubscriptionId, invalidSubject, validContent);
            });
            assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
            assertEquals("Нельзя отправить сообщение без темы", exception.getDescription());
            verifyNoInteractions(subscriptionService, userService, messageService);
        }

        @Test
        @DisplayName("Should throw ServerException BAD_REQUEST for empty subject")
        void testSendMessageToSubscribers_EmptySubject() {
            String invalidSubject = "";

            ServerException exception = assertThrows(ServerException.class, () -> {
                subscriptionController.sendMessageToSubscribers(validSubscriptionId, invalidSubject, validContent);
            });
            assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
            assertEquals("Нельзя отправить сообщение без темы", exception.getDescription());
            verifyNoInteractions(subscriptionService, userService, messageService);
        }

        @Test
        @DisplayName("Should throw ServerException BAD_REQUEST for null content")
        void testSendMessageToSubscribers_NullContent() {
            String invalidContent = null;

            ServerException exception = assertThrows(ServerException.class, () -> {
                subscriptionController.sendMessageToSubscribers(validSubscriptionId, validSubject, invalidContent);
            });
            assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
            assertEquals("Нельзя отправить пустое сообщение", exception.getDescription());
            verifyNoInteractions(subscriptionService, userService, messageService);
        }

        @Test
        @DisplayName("Should throw ServerException BAD_REQUEST for empty content")
        void testSendMessageToSubscribers_EmptyContent() {
            String invalidContent = "";

            ServerException exception = assertThrows(ServerException.class, () -> {
                subscriptionController.sendMessageToSubscribers(validSubscriptionId, validSubject, invalidContent);
            });
            assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
            assertEquals("Нельзя отправить пустое сообщение", exception.getDescription());
            verifyNoInteractions(subscriptionService, userService, messageService);
        }

        @Test
        @DisplayName("Should throw ServerException NOT_FOUND when subscription is not found")
        void testSendMessageToSubscribers_SubscriptionNotFound() {
            when(subscriptionService.findById(validSubscriptionId)).thenReturn(Optional.empty());

            ServerException exception = assertThrows(ServerException.class, () -> {
                subscriptionController.sendMessageToSubscribers(validSubscriptionId, validSubject, validContent);
            });

            assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
            assertEquals("Такой рассылки не существует", exception.getDescription());
            verify(subscriptionService).findById(validSubscriptionId);
            verify(messageService, never()).sendMessageToSubscribers(anyString(), anyString(), any());
        }
    }
}