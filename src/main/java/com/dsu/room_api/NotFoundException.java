package com.dsu.room_api;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}