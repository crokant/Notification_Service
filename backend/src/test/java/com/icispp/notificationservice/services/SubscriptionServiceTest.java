package com.icispp.notificationservice.services;

import com.icispp.notificationservice.models.Subscription;
import com.icispp.notificationservice.models.User;
import com.icispp.notificationservice.repositories.SqlSubscriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    @Mock
    private SqlSubscriptionRepository subscriptionRepository;

    @InjectMocks
    private SubscriptionService subscriptionService;

    private User testUser;
    private Subscription testSubscription;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .name("Test User")
                .email("test@example.com")
                .build();

        testSubscription = Subscription.builder()
                .id(1L)
                .name("Test Subscription")
                .creator(testUser)
                .build();
    }

    @Test
    void findById_WhenSubscriptionExists_ShouldReturnSubscription() {
        // Arrange
        when(subscriptionRepository.findSubscriptionById(1L))
                .thenReturn(Optional.of(testSubscription));

        // Act
        Optional<Subscription> result = subscriptionService.findById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testSubscription, result.get());
        verify(subscriptionRepository).findSubscriptionById(1L);
    }

    @Test
    void findById_WhenSubscriptionNotExists_ShouldReturnEmpty() {
        // Arrange
        when(subscriptionRepository.findSubscriptionById(anyLong()))
                .thenReturn(Optional.empty());

        // Act
        Optional<Subscription> result = subscriptionService.findById(999L);

        // Assert
        assertTrue(result.isEmpty());
        verify(subscriptionRepository).findSubscriptionById(999L);
    }

    @Test
    void addUserToSubscription_ShouldAddUserToSubscription() {
        // Arrange
        User newUser = User.builder()
                .id(2L)
                .name("New User")
                .build();

        // Act
        Subscription result = subscriptionService.addUserToSubscription(newUser, testSubscription);

        // Assert
        verify(subscriptionRepository).addSubscriptionToUser(2L, 1L);
        assertTrue(result.getSubscribers().contains(newUser));
    }

    @Test
    void createSubscription_ShouldCreateNewSubscription() {
        // Arrange
        String subscriptionName = "New Subscription";
        when(subscriptionRepository.createSubscription(anyLong(), any(Subscription.class)))
                .thenAnswer(invocation -> {
                    Subscription s = invocation.getArgument(1);
                    s.setId(2L);
                    return s;
                });

        // Act
        Subscription result = subscriptionService.createSubscription(subscriptionName, testUser);

        // Assert
        assertNotNull(result.getId());
        assertEquals(subscriptionName, result.getName());
        assertEquals(testUser, result.getCreator());
        verify(subscriptionRepository).createSubscription(eq(1L), any(Subscription.class));
    }

}