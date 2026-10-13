package com.dsu.room_api;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository  extends JpaRepository<Reservation, Long> {
    List<Reservation> findByRoomIdOrderByStartTime(Long roomId);
    List<Reservation> findByReservedByOrderByStartTime(String reservedBy);
    boolean existsByRoomId(Long roomId);
}