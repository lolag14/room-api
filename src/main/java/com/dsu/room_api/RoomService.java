package com.dsu.room_api;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoomService {
    private final RoomRepository roomRepository;
    private void validateCapacity(int capacity) {
        if (capacity < 1 || capacity > 20) {
            throw new InvalidCapacityException("Capacity must be between 1 and 20");
        }
    }

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public List<Room> findAll(Integer minCapacity, String keyword) {
        if (minCapacity == null) {
            return roomRepository.findByNameContainingIgnoreCase(keyword);
        }
        return roomRepository.findByCapacityGreaterThanEqualAndNameContainingIgnoreCase(minCapacity, keyword);

    }

    public Optional<Room> findById(Long id) {
        return roomRepository.findById(id);
    }

    public Room create(RoomCreateRequest request) {
        validateCapacity(request.capacity());
        return roomRepository.save(new Room(null, request.name(), request.capacity()));
    }

    public Optional<Room> update(Long id, RoomCreateRequest request) {
        validateCapacity(request.capacity());
        if (!roomRepository.existsById(id)) {
            return Optional.empty();
        }
        return Optional.of(roomRepository.save(new Room(id, request.name(), request.capacity())));
    }

    public boolean delete(Long id) {
        if (!roomRepository.existsById(id)) {
            return false;
        }
        roomRepository.deleteById(id);
        return true;
    }
}