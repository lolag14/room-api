package com.dsu.room_api;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> invalidFields(
            MethodArgumentNotValidException e, HttpServletRequest request) {
        List<ApiError.FieldMessage> fields = e.getBindingResult()
                .getFieldErrors().stream()
                .map(f -> new ApiError.FieldMessage(
                        f.getField(), f.getDefaultMessage()))
                .toList();
        return respond(HttpStatus.BAD_REQUEST,
                "request has invalid fields", request, fields);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> notFound(
            NotFoundException e, HttpServletRequest request) {
        return respond(HttpStatus.NOT_FOUND,
                e.getMessage(), request, List.of());
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiError> conflict(
            ConflictException e, HttpServletRequest request) {
        return respond(HttpStatus.CONFLICT,
                e.getMessage(), request, List.of());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> badRequest(
            IllegalArgumentException e, HttpServletRequest request) {
        return respond(HttpStatus.BAD_REQUEST,
                e.getMessage(), request, List.of());
    }

    private ResponseEntity<ApiError> respond(HttpStatus status, String message,
                                             HttpServletRequest request,
                                             List<ApiError.FieldMessage> fieldErrors) {
        return ResponseEntity.status(status)
                .body(ApiError.of(status, message,
                        request.getRequestURI(), fieldErrors));
    }
}