package com.dsu.room_api;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByRoomIdOrderByStartTime(Long roomId);
    List<Reservation> findByReservedByOrderByStartTime(String reservedBy);
    boolean existsByRoomId(Long roomId);

    @Query("""
            select r from Reservation r
            join fetch r.room
            where r.reservedBy = :name
            order by r.startTime
            """)
    List<Reservation> findByReservedByWithRoom(@Param("name") String name);
}