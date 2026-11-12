package com.dsu.room_api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Tag(name = "Rooms", description = "Room listings")
@RestController
@RequestMapping("/api/rooms")
public class RoomController {
    private final RoomService roomService;
    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @Operation(summary = "Finds all rooms with an optional minimum capacity and/or name filter")
    @ApiResponse(responseCode = "200", description = "Rooms listed")
    @GetMapping
    public List<RoomResponse> getAllRooms(
            @RequestParam(required = false) Integer minCapacity,
            @RequestParam(defaultValue = "") String keyword) {

        return roomService.search(minCapacity, keyword).stream()
                .map(RoomResponse::from)
                .toList();
    }

    @Operation(summary = "Finds a room by id")
    @ApiResponse(responseCode = "200", description = "Room found")
    @ApiResponse(responseCode = "404", description = "Room not found")
    @GetMapping("/{id}")
    public ResponseEntity<RoomResponse> getRoomById(@PathVariable Long id) {
        return roomService.findById(id)
                .map(r -> ResponseEntity.ok(RoomResponse.from(r)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Creates a room with provided data and assigns it an id")
    @ApiResponse(responseCode = "400", description = "Capacity must be between 1 and 20")
    @ApiResponse(responseCode = "201", description = "Room created, location header points to it")
    @PostMapping
    public ResponseEntity<RoomResponse> createRoom(@RequestBody RoomCreateRequest request) {
        Room newRoom = roomService.create(request);
        return ResponseEntity
                .created(URI.create("/api/rooms/" + newRoom.getId()))
                .body(RoomResponse.from(newRoom));
    }

    @Operation(summary = "Updates an existing room by id with provided data")
    @ApiResponse(responseCode = "200", description = "Room updated")
    @ApiResponse(responseCode = "400", description = "Capacity must be between 1 and 20")
    @ApiResponse(responseCode = "404", description = "Room not found")
    @PutMapping("/{id}")
    public ResponseEntity<RoomResponse> updateRoom(@PathVariable Long id, @RequestBody RoomCreateRequest request) {
        return roomService.update(id, request)
                .map(r -> ResponseEntity.ok(RoomResponse.from(r)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Deletes a room by id")
    @ApiResponse(responseCode = "204", description = "Room deleted")
    @ApiResponse(responseCode = "404", description = "Room not found")
    @ApiResponse(responseCode = "409", description = "Room has reservations")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
        boolean removed = roomService.delete(id);
        return removed ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Counts all rooms")
    @ApiResponse(responseCode = "200", description = "Number of rooms")
    @GetMapping("/count")
    public long countRooms() {
        return roomService.count();
    }

}