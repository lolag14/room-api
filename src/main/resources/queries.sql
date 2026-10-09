-- Q1 Rooms for 6 or more people, largest first
SELECT *
FROM room
WHERE capacity >= 6
ORDER BY capacity DESC

-- Q2 Every reservation made by Mina
SELECT *
FROM reservation
WHERE reserved_by = 'Mina'

-- Q3 Every reservation with its room's name
SELECT reservation.*, room.name AS room_name
FROM reservation
         JOIN room ON room.id = reservation.room_id;

-- Q4 Seminar A's reservation on 6 October 2026
SELECT reservation.*
FROM reservation
         JOIN room ON room.id = reservation.room_id
WHERE room.name = 'Seminar A'
  AND reservation.start_time >= '2026-10-06'
  AND reservation.start_time < '2026-10-07';

-- Q5 Reservations per room, with JOIN
SELECT r.name, COUNT(res.id) AS reservation_count
FROM room r
         JOIN reservation res ON res.room_id = r.id
GROUP BY r.id, r.name
ORDER BY r.id;

-- Q6 Same as Q5, but every room —0 included
SELECT room.name, COUNT(reservation.id) AS reservation_count
FROM room
         LEFT JOIN reservation ON reservation.room_id = room.id
GROUP BY room.id, room.name
ORDER BY room.id;

-- Q7 Rooms that have never been reserved
SELECT room.*
FROM room
         LEFT JOIN reservation ON reservation.room_id = room.id
WHERE reservation.id IS NULL
ORDER BY room.id;

-- Q8 Rooms with more than two reservations
SELECT room.*
FROM room
         JOIN reservation ON reservation.room_id = room.id
GROUP BY room.id
HAVING COUNT(reservation.id) > 2
ORDER BY room.id;