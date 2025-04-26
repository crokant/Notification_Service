package com.icispp.notificationservice.controllers;

import com.icispp.notificationservice.dto.AuthRequest;
import com.icispp.notificationservice.dto.AuthResponse;
import com.icispp.notificationservice.dto.RegisterRequest;
import com.icispp.notificationservice.exception.ServerException;
import com.icispp.notificationservice.models.User;
import com.icispp.notificationservice.services.UserService;
import com.icispp.notificationservice.util.JwtUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserService userService;

    @Test
    @DisplayName("Регистрация должна проходить успешно и возвращать статус OK с сообщением")
    public void testRegister_Success() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testUser");
        request.setEmail("test@example.com");
        request.setPassword("password");

        when(userService.wasUsernameUsed(request.getUsername())).thenReturn(false);
        User user = new User();

        doReturn(user).when(userService).registerUser(any(String.class), any(String.class), any(String.class));

        ResponseEntity<String> response = authController.register(request);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Пользователь успешно зарегистрирован", response.getBody());

        verify(userService, times(1)).wasUsernameUsed("testUser");
        verify(userService, times(1)).registerUser("testUser", "test@example.com", "password");
    }

    @Test
    @DisplayName("Регистрация должна возвращать CONFLICT, если имя пользователя уже занято")
    public void testRegister_Conflict() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("existingUser");
        request.setEmail("test@example.com");
        request.setPassword("password");

        when(userService.wasUsernameUsed(request.getUsername())).thenReturn(true);

        ServerException thrown = assertThrows(ServerException.class, () -> {
            authController.register(request);
        }, "Ожидалось ServerException при существующем пользователе");

        assertEquals(HttpStatus.CONFLICT, thrown.getStatusCode());
        assertTrue(thrown.getMessage().contains("Пользователь с таким именем уже существует"));

        verify(userService, times(1)).wasUsernameUsed("existingUser");
        verify(userService, never()).registerUser(any(String.class), any(String.class), any(String.class));
    }

    @Test
    @DisplayName("Логин должен проходить успешно и возвращать токен")
    public void testLogin_Success() {
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername("testUser");
        authRequest.setPassword("password");

        Authentication mockedAuthentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockedAuthentication);
        when(jwtUtil.generateToken("testUser")).thenReturn("mockToken");

        ResponseEntity<AuthResponse> response = authController.login(authRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("mockToken", response.getBody().getToken());

        ArgumentCaptor<UsernamePasswordAuthenticationToken> authCaptor = ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
        verify(authenticationManager, times(1)).authenticate(authCaptor.capture());
        assertEquals("testUser", authCaptor.getValue().getName());

        verify(jwtUtil, times(1)).generateToken("testUser");
    }

    @Test
    @DisplayName("Логин должен возвращать UNAUTHORIZED при неверных учетных данных")
    public void testLogin_Failure() {
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername("testUser");
        authRequest.setPassword("wrongPassword");

        doThrow(new BadCredentialsException("Неверные учетные данные"))
                .when(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));

        ServerException thrown = assertThrows(ServerException.class, () -> {
            authController.login(authRequest);
        }, "Ожидалось ServerException при неудачной аутентификации");

        assertEquals(HttpStatus.UNAUTHORIZED, thrown.getStatusCode());
        assertTrue(thrown.getMessage().contains("Неверные учетные данные"));

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtil, never()).generateToken(anyString());
    }
}