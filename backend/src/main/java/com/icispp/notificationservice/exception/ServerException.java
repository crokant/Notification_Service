package com.icispp.notificationservice.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor
public class ServerException extends RuntimeException {
    String description;
    HttpStatus statusCode;

    public ServerException(HttpStatus statusCode , String description) {
        super(description);
        this.description = description;
        this.statusCode = statusCode;
    }
}
