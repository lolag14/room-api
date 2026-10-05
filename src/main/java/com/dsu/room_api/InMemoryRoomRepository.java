package com.dsu.room_api;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryRoomRepository implements RoomRepository {
    private final Map<Long, Room> rooms = new ConcurrentSkipListMap<>();
    private final AtomicLong nextId = new AtomicLong(1);

    public InMemoryRoomRepository() {
        save(new Room(null, "Seminar A", 8));
        save(new Room(null, "Study Pod", 4));
        save(new Room(null, "Rooftop Room", 12));
    }

    @Override
    public List<Room> findAll() {
        return new ArrayList<>(rooms.values());
    }

    @Override
    public Optional<Room> findById(Long id)
    {
        return Optional.ofNullable(rooms.get(id));
    }

    @Override
    public boolean existsById(Long id)
    {
        return rooms.containsKey(id);
    }

    @Override
    public void deleteById(Long id)
    {
        rooms.remove(id);
    }

    @Override
    public Room save(Room room)
    {
        Long id = room.id() == null ? nextId.getAndIncrement() : room.id();
        Room newRoom = new Room(id, room.name(), room.capacity());
        rooms.put(id, newRoom);
        return newRoom;
    }
}

