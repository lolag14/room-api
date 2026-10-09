package com.dsu.room_api;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByNameContainingIgnoreCase(String keyword);
    List<Room> findByCapacityGreaterThanEqualAndNameContainingIgnoreCase(
            int minCapacity, String keyword);
}