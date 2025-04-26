package com.icispp.notificationservice.services;

import com.icispp.notificationservice.models.User;
import com.icispp.notificationservice.repositories.SqlUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private SqlUserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("testUser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("plainPassword");
    }

    @Test
    void testFindById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        verify(userRepository).findById(1L);
    }

    @Test
    void testFindById_NotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<User> result = userService.findById(1L);

        assertTrue(result.isEmpty());
        verify(userRepository).findById(1L);
    }

    @Test
    void testRegisterUser_Success() {
        when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");
        when(userRepository.saveUser(any(User.class))).thenReturn(testUser);

        User result = userService.registerUser(testUser);

        assertNotNull(result);
        assertEquals("encodedPassword", result.getPassword());

        verify(passwordEncoder).encode("plainPassword");
        verify(userRepository).saveUser(any(User.class));
    }


    @Test
    void testFindByUsername_Success() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.findByName("testUser");

        assertTrue(result.isPresent());
        assertEquals("testUser", result.get().getName());
        verify(userRepository).findByUsername("testUser");
    }

    @Test
    void testFindByUsername_NotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        Optional<User> result = userService.findByName("unknown");

        assertTrue(result.isEmpty());
        verify(userRepository).findByUsername("unknown");
    }

    @Test
    void testValidatePassword_CorrectPassword() {
        when(passwordEncoder.matches("plainPassword", "encodedPassword"))
                .thenReturn(true);

        boolean isValid = userService.validatePassword("plainPassword", "encodedPassword");

        assertTrue(isValid);
        verify(passwordEncoder).matches("plainPassword", "encodedPassword");
    }

    @Test
    void testValidatePassword_WrongPassword() {
        when(passwordEncoder.matches("wrongPassword", "encodedPassword"))
                .thenReturn(false);

        boolean isValid = userService.validatePassword("wrongPassword", "encodedPassword");

        assertFalse(isValid);
        verify(passwordEncoder).matches("wrongPassword", "encodedPassword");
    }

    @Test
    void testRegisterUserWithDetails_Success() {
        when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");
        when(userRepository.saveUser(any(User.class))).thenReturn(testUser);

        User result = userService.registerUser("testUser", "test@example.com", "plainPassword");

        assertNotNull(result);
        assertEquals("testUser", result.getName());
        assertEquals("test@example.com", result.getEmail());
        verify(passwordEncoder).encode("plainPassword");
        verify(userRepository).saveUser(any(User.class));
    }
}