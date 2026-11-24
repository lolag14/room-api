package com.dsu.room_api;

import java.time.LocalDateTime;

public record ReservationResponse(Long id, Long roomId, String roomName,
                                  String owner, LocalDateTime startTime,
                                  LocalDateTime endTime) {
    public static ReservationResponse from(Reservation r) {
        return new ReservationResponse(
                r.getId(),
                r.getRoom().getId(),
                r.getRoom().getName(),
                r.getOwner().getUsername(),
                r.getStartTime(),
                r.getEndTime());
    }
}