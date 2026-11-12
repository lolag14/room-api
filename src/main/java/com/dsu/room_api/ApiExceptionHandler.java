package com.dsu.room_api;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

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

    // request 40: the body is not valid JSON, or a value has the wrong type
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> unreadableBody(
            HttpMessageNotReadableException e, HttpServletRequest request) {
        return respond(HttpStatus.BAD_REQUEST,
                "request body is not valid JSON", request, List.of());
    }

    // request 41: /api/rooms/abc, the path value cannot become a Long
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> wrongType(
            MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        return respond(HttpStatus.BAD_REQUEST,
                "'" + e.getName() + "' has the wrong type", request, List.of());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> other(Exception e, HttpServletRequest request) {
        // Spring's own exceptions (unknown URL, wrong method, missing parameter)
        if (e instanceof ErrorResponse spring) {
            HttpStatus status = HttpStatus.valueOf(spring.getStatusCode().value());
            return respond(status, spring.getBody().getDetail(), request, List.of());
        }
        // a real bug: details in the log, none in the response
        log.error("Unexpected error on {} {}", request.getMethod(),
                request.getRequestURI(), e);
        return respond(HttpStatus.INTERNAL_SERVER_ERROR,
                "unexpected server error", request, List.of());
    }

    private ResponseEntity<ApiError> respond(HttpStatus status, String message,
                                             HttpServletRequest request,
                                             List<ApiError.FieldMessage> fieldErrors) {
        return ResponseEntity.status(status)
                .body(ApiError.of(status, message,
                        request.getRequestURI(), fieldErrors));
    }
}