package com.dsu.room_api;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class RoomRepository {
    private final Map<Long, Room> rooms = new ConcurrentHashMap<>();
    public List<Room> findAll() {
        return new ArrayList<>(rooms.values());
    }
    public Optional<Room> findById(Long id)
    {
        return Optional.ofNullable(rooms.get(id));
    }
}