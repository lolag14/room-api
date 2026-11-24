package com.dsu.room_api;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservationService {
    private final RoomRepository roomRepository;
    private final ReservationRepository reservationRepository;
    private final AppUserRepository userRepository;

    public ReservationService(RoomRepository roomRepository,
                              ReservationRepository reservationRepository,
                              AppUserRepository userRepository) {
        this.roomRepository = roomRepository;
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
    }

    public List<Reservation> findByRoom(Long roomId) {
        if (!roomRepository.existsById(roomId)) {
            throw new NotFoundException("room " + roomId + " not found");
        }
        return reservationRepository.findByRoomIdOrderByStartTime(roomId);
    }

    public List<Reservation> findMine(String username) {
        return reservationRepository.findMine(username);
    }

    // 404 first, then 403: the owner or an admin may read
    @Transactional(readOnly = true)
    public Reservation get(Long id, String username, boolean admin) {
        Reservation r = find(id);
        requireOwnerOrAdmin(r, id, username, admin);
        return r;
    }

    @Transactional
    public Reservation create(Long roomId, String username,
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

        AppUser owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("user " + username + " not found"));

        return reservationRepository.save(
                new Reservation(room, owner, startTime, endTime));
    }

    // the owner or an admin may cancel
    @Transactional
    public void delete(Long id, String username, boolean admin) {
        Reservation r = find(id);                         // 404 first
        requireOwnerOrAdmin(r, id, username, admin);      // then 403
        reservationRepository.delete(r);
    }

    // owner only: not even an admin may move someone's booking
    @Transactional
    public Reservation update(Long id, String username,
                              LocalDateTime startTime, LocalDateTime endTime) {
        if (!startTime.isBefore(endTime)) {
            throw new IllegalArgumentException("startTime must be before endTime");   // 400
        }

        Reservation r = find(id);                                                      // 404

        requireOwner(r, id, username);                                                 // 403

        if (reservationRepository.countOverlappingExcept(
                r.getRoom().getId(), id, startTime, endTime) > 0) {
            throw new ConflictException(
                    "room " + r.getRoom().getId() + " is already booked at that time");   // 409
        }

        r.reschedule(startTime, endTime);   // no save(): dirty checking writes the update
        return r;
    }

    private void requireOwner(Reservation r, Long id, String username) {
        if (!r.isOwnedBy(username)) {
            throw new ForbiddenException("reservation " + id + " belongs to another user");
        }
    }

    private Reservation find(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("reservation " + id + " not found"));
    }

    private void requireOwnerOrAdmin(Reservation r, Long id, String username, boolean admin) {
        if (!admin && !r.isOwnedBy(username)) {
            throw new ForbiddenException("reservation " + id + " belongs to another user");
        }
    }
}