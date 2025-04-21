package com.icispp.notificationservice.controllers;

import com.icispp.notificationservice.dto.UserInfoResponse;
import com.icispp.notificationservice.exception.ServerException;
import com.icispp.notificationservice.models.User;
import com.icispp.notificationservice.services.UserService;
import com.icispp.notificationservice.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonalOfficeControllerTest {

    @InjectMocks
    private PersonalOfficeController personalOfficeController;

    @Mock
    private UserService userService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private HttpServletRequest request;

    @Test
    void testGetUserInfo_Success() {
        String token = "valid.token";
        String username = "testUser";
        User user = new User();
        user.setId(1L);
        user.setName(username);
        user.setEmail("test@example.com");

        when(jwtUtil.resolveToken(request)).thenReturn(token);
        when(jwtUtil.validateToken(token)).thenReturn(true);
        when(jwtUtil.getUsernameFromToken(token)).thenReturn(username);
        when(userService.findByName(username)).thenReturn(Optional.of(user));

        ResponseEntity<?> response = personalOfficeController.getUserInfo(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof UserInfoResponse, "Тело ответа должно быть типа UserInfoResponse");
        UserInfoResponse userInfoResponse = (UserInfoResponse) response.getBody();
        assertEquals("testUser", userInfoResponse.getName());
        assertEquals("test@example.com", userInfoResponse.getEmail());

        verify(jwtUtil).resolveToken(request);
        verify(jwtUtil).validateToken(token);
        verify(jwtUtil).getUsernameFromToken(token);
        verify(userService).findByName(username);
    }

    @Test
    void testGetUserInfo_TokenNotResolved() {
        when(jwtUtil.resolveToken(request)).thenReturn(null);

        ServerException exception = assertThrows(ServerException.class, () -> {
            personalOfficeController.getUserInfo(request);
        });

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        assertEquals("Неверный токен", exception.getDescription());

        verify(jwtUtil).resolveToken(request);
        verify(jwtUtil, never()).validateToken(any());
        verify(jwtUtil, never()).getUsernameFromToken(any());
        verify(userService, never()).findByName(any());
    }


    @Test
    void testGetUserInfo_InvalidToken() {
        String token = "invalid.token";

        when(jwtUtil.resolveToken(request)).thenReturn(token);
        when(jwtUtil.validateToken(token)).thenReturn(false);

        ServerException exception = assertThrows(ServerException.class, () -> {
            personalOfficeController.getUserInfo(request);
        });

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        assertEquals("Неверный токен", exception.getDescription());


        verify(jwtUtil).resolveToken(request);
        verify(jwtUtil).validateToken(token);

        verify(jwtUtil, never()).getUsernameFromToken(any());
        verify(userService, never()).findByName(any());
    }

    @Test
    void testGetUserInfo_UserNotFound() {

        String token = "valid.token";
        String username = "nonExistentUser";

        when(jwtUtil.resolveToken(request)).thenReturn(token);
        when(jwtUtil.validateToken(token)).thenReturn(true);
        when(jwtUtil.getUsernameFromToken(token)).thenReturn(username);
        when(userService.findByName(username)).thenReturn(Optional.empty());
        ServerException exception = assertThrows(ServerException.class, () -> {
            personalOfficeController.getUserInfo(request);
        });


        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());

        assertEquals("Пользователь не найден", exception.getDescription());

        verify(jwtUtil).resolveToken(request);
        verify(jwtUtil).validateToken(token);
        verify(jwtUtil).getUsernameFromToken(token);
        verify(userService).findByName(username);
    }
}