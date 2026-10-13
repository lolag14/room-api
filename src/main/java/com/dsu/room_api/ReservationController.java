package com.dsu.room_api;

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

    @GetMapping("/rooms/{roomId}/reservations")
    public ResponseEntity<List<ReservationResponse>>
    listForRoom(@PathVariable Long roomId) {
        return reservationService.findByRoom(roomId)
                .map(list -> ResponseEntity.ok(list.stream()
                        .map(ReservationResponse::from).toList()))
                .orElseGet(() ->
                        ResponseEntity.notFound().build());
    }

    @PostMapping("/rooms/{roomId}/reservations")
    public ResponseEntity<ReservationResponse> create(
            @PathVariable Long roomId,
            @RequestBody ReservationCreateRequest request) {
        try {
            return reservationService.create(roomId, request.reservedBy(),
                            request.startTime(), request.endTime())
                    .map(r -> ResponseEntity
                            .created(URI.create("/api/reservations/" + r.getId()))
                            .body(ReservationResponse.from(r)))
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}