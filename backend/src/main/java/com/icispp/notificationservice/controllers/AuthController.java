package com.icispp.notificationservice.controllers;

import com.icispp.notificationservice.dto.AuthRequest;
import com.icispp.notificationservice.dto.AuthResponse;
import com.icispp.notificationservice.dto.RegisterRequest;
import com.icispp.notificationservice.exception.ServerException;
import com.icispp.notificationservice.models.User;
import com.icispp.notificationservice.services.UserService;
import com.icispp.notificationservice.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Контроллер для аутентификации пользователей.
 * Предоставляет методы для регистрации и входа в систему.
 */
@RestController
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    /**
     * Конструктор класса AuthController.
     *
     * @param authenticationManager Менеджер аутентификации
     * @param jwtUtil Утилита для работы с JWT
     * @param userService Сервис для работы с пользователями
     */
    @Autowired
    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    /**
     * Регистрация нового пользователя.
     * Проверяет, существует ли пользователь с указанным именем.
     * Если существует, возвращает статус 409 (Conflict).
     * Если регистрация успешна, возвращает статус 200 (OK).
     * В случае ошибки возвращает статус 400 (Bad Request).
     *
     * @param request Запрос на регистрацию, содержащий имя пользователя, email и пароль
     * @return ResponseEntity с сообщением о результате регистрации
     */
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        if (userService.wasUsernameUsed(request.getUsername())) {
            throw new ServerException(HttpStatus.CONFLICT, "Пользователь с таким именем уже существует" );
        }

        userService.registerUser(request.getUsername(), request.getEmail(), request.getPassword());
        log.info("Пользователь успешно зарегистрирован: " + request.getUsername() + " " + request.getEmail());

        return ResponseEntity.ok("Пользователь успешно зарегистрирован");
    }

    /**
     * Вход пользователя в систему.
     * Проверяет учетные данные пользователя и, если они верны, генерирует JWT токен.
     * В случае успешной аутентификации возвращает статус 200 (OK) с токеном.
     * Если аутентификация не удалась, возвращает статус 401 (Unauthorized).
     *
     * @param authRequest Запрос на аутентификацию, содержащий имя пользователя и пароль
     * @return ResponseEntity с JWT токеном или сообщением об ошибке
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest authRequest) {
        log.info(authRequest.getUsername() + " " + authRequest.getPassword());
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );
            String token = jwtUtil.generateToken(authRequest.getUsername());
            return ResponseEntity.ok(new AuthResponse(token));
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServerException(HttpStatus.UNAUTHORIZED , "Неверные учетные данные");
        }
    }
}
