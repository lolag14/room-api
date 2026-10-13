package com.dsu.room_api;

import org.springframework.stereotype.Service;

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

    public Optional<List<Reservation>> findByRoom(
            Long roomId) {
        if (!roomRepository.existsById(roomId)) {
            return Optional.empty();
        }
        return Optional.of(reservationRepository.findByRoomIdOrderByStartTime(roomId));
    }
}
