package com.dsu.room_api;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.time.LocalDateTime;

@Entity
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_id")
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id")
    private AppUser owner;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    protected Reservation() { }
    public Reservation(Room room, AppUser owner, LocalDateTime startTime, LocalDateTime endTime) {
        this.room = room;
        this.owner = owner;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public boolean isOwnedBy(String username) {
        return owner.getUsername().equals(username);
    }

    public void reschedule(LocalDateTime startTime, LocalDateTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Long getId() { return id; }
    public Room getRoom() { return room; }
    public AppUser getOwner() { return owner; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
}