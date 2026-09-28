package com.dsu.room_api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {
    private final RoomRepository roomRepository;
    public RoomController(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @GetMapping
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    @GetMapping("/{id}")
    public Room getRoomById(@PathVariable Long id) {
        return roomRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Room> createRoom(@RequestBody Room room) {
        Room newRoom = roomRepository.save(room);
        URI location = URI.create("/api/rooms/" + newRoom.id());
        return ResponseEntity.created(location).body(newRoom);
    }

    @PutMapping("/{id}")
    public Room updateRoom(@RequestBody Room room, @PathVariable Long id) {
        if (!roomRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        Room updatedRoom = new Room(id, room.name(), room.capacity());
        return roomRepository.save(updatedRoom);
    }

    @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
        if (!roomRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        roomRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}