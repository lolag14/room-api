package com.dsu.room_api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class RoomHasReservationsException extends RuntimeException {
    public RoomHasReservationsException(String message) {
        super(message);
    }
}