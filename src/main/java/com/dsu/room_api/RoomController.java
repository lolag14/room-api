package com.dsu.room_api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Tag(name = "Rooms", description = "Room listings")
@RestController
@RequestMapping("/api/rooms")
public class RoomController {
    private final List<Room> rooms = new ArrayList<>(List.of(
            new Room(1L, "Seminar A", 8),
            new Room(2L, "Study Pod", 4),
            new Room(3L, "Rooftop Room", 12)));
    private final AtomicLong nextId = new AtomicLong(4);

    @Operation(summary = "Finds all rooms with an optional minimum capacity and/or name filter")
    @ApiResponse(responseCode = "200", description = "Rooms listed")
    @GetMapping
    public List<Room> getAllRooms(
            @RequestParam(required = false) Integer minCapacity,
            @RequestParam(defaultValue = "") String keyword) {

        return rooms.stream()
                .filter(r -> minCapacity == null || r.capacity() >= minCapacity)
                .filter(r -> r.name().toLowerCase().contains(keyword.toLowerCase()))
                .toList();
    }

    @Operation(summary = "Finds a room by id")
    @ApiResponse(responseCode = "200", description = "Room found")
    @ApiResponse(responseCode = "404", description = "Room not found")
    @GetMapping("/{id}")
    public ResponseEntity<Room> getRoomById(@PathVariable Long id) {
        Optional<Room> foundRoom;
        foundRoom = rooms.stream().filter(r -> r.id().equals(id)).findFirst();
        return foundRoom.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Creates a room with provided data and assigns it an id")
    @ApiResponse(responseCode = "201", description = "Room created, location header points to it")
    @PostMapping
    public ResponseEntity<Room> createRoom(@RequestBody RoomCreateRequest request) {
        Room newRoom = new Room(nextId.getAndIncrement(), request.name(), request.capacity());
        rooms.add(newRoom);
        return ResponseEntity
                .created(URI.create("/api/rooms/" + newRoom.id()))
                .body(newRoom);
    }

    @Operation(summary = "Updates an existing room by id with provided data")
    @ApiResponse(responseCode = "200", description = "Room updated")
    @ApiResponse(responseCode = "404", description = "Room not found")
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

    @Operation(summary = "Deletes a room by id")
    @ApiResponse(responseCode = "204", description = "Room deleted")
    @ApiResponse(responseCode = "404", description = "Room not found")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
        boolean removed = rooms.removeIf(r -> r.id().equals(id));
        return removed ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

}