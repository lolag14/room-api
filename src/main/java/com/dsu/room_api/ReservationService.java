package com.dsu.room_api;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservationService {
    private final RoomRepository roomRepository;
    private final ReservationRepository reservationRepository;

    public ReservationService(RoomRepository roomRepository, ReservationRepository reservationRepository) {
        this.roomRepository = roomRepository;
        this.reservationRepository = reservationRepository;
    }

    public List<Reservation> findByRoom(Long roomId) {
        if (!roomRepository.existsById(roomId)) {
            throw new NotFoundException("room " + roomId + " not found");
        }
        return reservationRepository.findByRoomIdOrderByStartTime(roomId);
    }

    public List<Reservation> findByReservedBy(String reservedBy) {
        return reservationRepository.findByReservedByWithRoom(reservedBy);
    }

    public Reservation get(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("reservation " + id + " not found"));
    }

    @Transactional
    public Reservation create(Long roomId, String reservedBy,
                              LocalDateTime startTime, LocalDateTime endTime) {
        if (!startTime.isBefore(endTime)) {
            throw new IllegalArgumentException("startTime must be before endTime");   // 400
        }

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new NotFoundException("room " + roomId + " not found"));   // 404

        if (reservationRepository.countOverlapping(roomId, startTime, endTime) > 0) {
            throw new ConflictException(
                    "room " + roomId + " is already booked at that time");   // 409
        }

        return reservationRepository.save(
                new Reservation(room, reservedBy, startTime, endTime));
    }

    public void delete(Long id) {
        if (!reservationRepository.existsById(id)) {
            throw new NotFoundException("reservation " + id + " not found");
        }
        reservationRepository.deleteById(id);
    }
}