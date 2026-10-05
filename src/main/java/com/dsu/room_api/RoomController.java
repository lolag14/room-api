package com.dsu.room_api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {
    private final List<Room> rooms = new ArrayList<>(List.of(
            new Room(1L, "Seminar A", 8),
            new Room(2L, "Study Pod", 4),
            new Room(3L, "Rooftop Room", 12)));
    private final AtomicLong nextId = new AtomicLong(4);

    @GetMapping
    public List<Room> getAllRooms(
            @RequestParam(required = false) Integer minCapacity,
            @RequestParam(defaultValue = "") String keyword) {

        return rooms.stream()
                .filter(r -> minCapacity == null || r.capacity() >= minCapacity)
                .filter(r -> r.name().toLowerCase().contains(keyword.toLowerCase()))
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Room> getRoomById(@PathVariable Long id) {
        Optional<Room> foundRoom;
        foundRoom = rooms.stream().filter(r -> r.id().equals(id)).findFirst();
        return foundRoom.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Room> createRoom(@RequestBody RoomCreateRequest request) {
        Room newRoom = new Room(nextId.getAndIncrement(), request.name(), request.capacity());
        rooms.add(newRoom);
        return ResponseEntity
                .created(URI.create("/api/rooms/" + newRoom.id()))
                .body(newRoom);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Room> updateRoom(@PathVariable Long id, @RequestBody RoomCreateRequest request) {
        for (int i = 0; i < rooms.size(); i++) {
            if (id.equals(rooms.get(i).id())) {
                Room updatedRoom = new Room(id, request.name(), request.capacity());
                rooms.set(i, updatedRoom);
                return ResponseEntity.ok(updatedRoom);
            }
        }
        return ResponseEntity.notFound().build();
    }

}