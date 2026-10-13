package com.dsu.room_api;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {
    private final RoomRepository roomRepository;
    private final ReservationRepository reservationRepository;

    public ReservationService(RoomRepository roomRepository, ReservationRepository reservationRepository) {
        this.roomRepository = roomRepository;
        this.reservationRepository = reservationRepository;
    }

    public Optional<List<Reservation>> findByRoom(Long roomId) {
        if (!roomRepository.existsById(roomId)) {
            return Optional.empty();
        }
        return Optional.of(reservationRepository.findByRoomIdOrderByStartTime(roomId));
    }

    public List<Reservation> findByReservedBy(String reservedBy) {
        return reservationRepository.findByReservedByOrderByStartTime(reservedBy);
    }

    @Transactional
    public Optional<Reservation> create(Long roomId, String reservedBy,
                                        LocalDateTime startTime, LocalDateTime endTime) {
        checkRequest(reservedBy, startTime, endTime);      // 400

        Optional<Room> room = roomRepository.findById(roomId);
        if (room.isEmpty()) {
            return Optional.empty();                       // 404
        }

        return Optional.of(reservationRepository.save(
                new Reservation(room.get(), reservedBy, startTime, endTime)));
    }

    private void checkRequest(String reservedBy, LocalDateTime startTime,
                              LocalDateTime endTime) {
        if (reservedBy == null || reservedBy.isBlank()) {
            throw new IllegalArgumentException("reservedBy is required");
        }
        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("startTime and endTime are required");
        }
        if (!startTime.isBefore(endTime)) {
            throw new IllegalArgumentException("startTime must be before endTime");
        }
    }
}