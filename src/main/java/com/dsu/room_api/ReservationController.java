package com.dsu.room_api;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ReservationController {
    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping("/reservations")
    public List<ReservationResponse> search(@RequestParam String reservedBy) {
        return reservationService.findByReservedBy(reservedBy).stream()
                .map(ReservationResponse::from)
                .toList();
    }

    @GetMapping("/rooms/{roomId}/reservations")
    public List<ReservationResponse> listForRoom(@PathVariable Long roomId) {
        return reservationService.findByRoom(roomId).stream()
                .map(ReservationResponse::from)
                .toList();
    }

    @PostMapping("/rooms/{roomId}/reservations")
    public ResponseEntity<ReservationResponse> create(
            @PathVariable Long roomId,
            @Valid @RequestBody ReservationCreateRequest request) {
        Reservation r = reservationService.create(roomId, request.reservedBy(),
                request.startTime(), request.endTime());
        return ResponseEntity
                .created(URI.create("/api/reservations/" + r.getId()))
                .body(ReservationResponse.from(r));
    }

    @GetMapping("/reservations/{id}")
    public ReservationResponse findOne(@PathVariable Long id) {
        return ReservationResponse.from(reservationService.get(id));
    }

    @DeleteMapping("/reservations/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        reservationService.delete(id);
    }
}