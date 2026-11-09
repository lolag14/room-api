package com.dsu.room_api;

public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}