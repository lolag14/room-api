package com.dsu.room_api;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ReservationCreateRequest(
        @NotNull(message = "startTime is required")
        LocalDateTime startTime,
        @NotNull(message = "endTime is required")
        LocalDateTime endTime) { }