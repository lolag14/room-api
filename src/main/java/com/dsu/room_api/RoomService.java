package com.dsu.room_api;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RoomService {
    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public long count() {
        return roomRepository.count();
    }

    public List<Room> search(Integer minCapacity, String keyword) {
        if (minCapacity == null) {
            return roomRepository.findByNameContainingIgnoreCase(keyword);
        }
        return roomRepository.findByCapacityGreaterThanEqualAndNameContainingIgnoreCase(minCapacity, keyword);
    }

    public Room get(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("room " + id + " not found"));
    }

    public Room create(RoomCreateRequest request) {
        return roomRepository.save(new Room(null, request.name(), request.capacity()));
    }

    @Transactional
    public Room update(Long id, String name, int capacity) {
        Room room = get(id);              // managed
        room.update(name, capacity);      // changed in memory
        return room;                      // UPDATE at commit
    }

    public void delete(Long id) {
        if (!roomRepository.existsById(id)) {
            throw new NotFoundException("room " + id + " not found");
        }
        try {
            roomRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("room " + id + " has reservations");
        }
    }
}