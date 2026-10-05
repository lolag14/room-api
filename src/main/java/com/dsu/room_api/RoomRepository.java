package com.dsu.room_api;

import java.util.List;
import java.util.Optional;

public interface RoomRepository {
    List<Room> findAll();

    Optional<Room> findById(Long id);

    boolean existsById(Long id);

    void deleteById(Long id);

    Room save(Room room);

}