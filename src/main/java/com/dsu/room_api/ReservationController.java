package com.dsu.room_api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}