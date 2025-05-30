package com.icispp.notificationservice.exception;

import com.icispp.notificationservice.dto.ExceptionResponse;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class ServerExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            @NotNull MethodArgumentNotValidException ex,
            @NotNull HttpHeaders headers,
            @NotNull HttpStatusCode status,
            @NotNull WebRequest request) {
        return handleIncorrectRequest(ex, status);
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(
            @NotNull TypeMismatchException ex,
            @NotNull HttpHeaders headers,
            @NotNull HttpStatusCode status,
            @NotNull WebRequest request) {
        return handleIncorrectRequest(ex, status);
    }

    @Override
    protected ResponseEntity<Object> handleServletRequestBindingException(
            @NotNull ServletRequestBindingException ex,
            @NotNull HttpHeaders headers,
            @NotNull HttpStatusCode status,
            @NotNull WebRequest request) {
        return handleIncorrectRequest(ex, status);
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(
            NoHandlerFoundException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        return handleIncorrectRequest(ex, status);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return handleIncorrectRequest(ex, status);
    }

    private ResponseEntity<Object> handleIncorrectRequest(Exception ex, HttpStatusCode status) {
        return new ResponseEntity<>(
                new ExceptionResponse(
                        400,
                        "Некорректные параметры запроса: " + ex.getMessage()
                ),
                status);
    }

    @ExceptionHandler(ServerException.class)
    public ResponseEntity<ExceptionResponse> handleScrapperException(ServerException ex) {
        return new ResponseEntity<>(
                new ExceptionResponse(ex.getStatusCode().value() , ex.getDescription()),
                ex.getStatusCode());
    }
}
