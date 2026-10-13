package com.dsu.room_api;

import java.time.LocalDateTime;

public record ReservationCreateRequest(String reservedBy,
                                       LocalDateTime startTime,
                                       LocalDateTime endTime) { }