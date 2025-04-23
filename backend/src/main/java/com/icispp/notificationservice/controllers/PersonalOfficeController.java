package com.icispp.notificationservice.controllers;


import com.icispp.notificationservice.dto.UserInfoResponse;
import com.icispp.notificationservice.exception.ServerException;
import com.icispp.notificationservice.models.Subscription;
import com.icispp.notificationservice.models.User;
import com.icispp.notificationservice.services.UserService;
import com.icispp.notificationservice.util.JwtUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@Tag(name = "Personal office API", description = "All what you need for personal office page")
@RestController
@RequestMapping("/api/user")
public class PersonalOfficeController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Autowired
    public PersonalOfficeController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/info")
    public ResponseEntity<UserInfoResponse> getUserInfo(HttpServletRequest request) {
        String token = jwtUtil.resolveToken(request);

        if(token == null || !jwtUtil.validateToken(token)){
            throw new ServerException(HttpStatus.UNAUTHORIZED, "Неверный токен");
        }
        String username = jwtUtil.getUsernameFromToken(token);

        Optional<User> userOptional = userService.findByName(username);

        if(userOptional.isEmpty()){
            throw new ServerException(HttpStatus.NOT_FOUND, "Пользователь не найден");
        }

        User user = userOptional.get();
        return ResponseEntity.ok(new UserInfoResponse(user.getName(), user.getEmail(), "user"));
    }

    @GetMapping("/mailings")
    public ResponseEntity<?> getMailings(HttpServletRequest request){
        String token = jwtUtil.resolveToken(request);
        if (token != null && jwtUtil.validateToken(token)) {
            String username = jwtUtil.getUsernameFromToken(token);

            Optional<User> userOptional = userService.findByName(username);
            if (userOptional.isPresent()) {
                List<Subscription> mailings = userService.getUserSubscriptions(username);

                return ResponseEntity.ok(mailings);
            }

        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Неверный токен или пользователь не найден.");
    }

}
