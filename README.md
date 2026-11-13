# Room API

The REST API application is for creating, deleting, reading and updating room entries. The list can be filtered by
minCapacity (returns rooms with at least that capacity) and/or by keyword (matching all or part of the room name,
ignoring case).

## REST API

| Method | Path                                | Status                                                                                                                  | Description                                                                         |
|--------|-------------------------------------|-------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------|
| GET    | /api/rooms/count                    | 200                                                                                                                     | Returns the number of rooms (runs `select count(*)`)                                |
| GET    | /api/rooms                          | 200, or 400 if minCapacity isn't a number                                                                               | Finds all rooms                                                                     |
| GET    | /api/rooms/{id}                     | 200, or 404 if missing                                                                                                  | Finds a specific room                                                               |
| POST   | /api/rooms                          | 201, or 400 if capacity is not between 1 and 20                                                                         | Creates a new room with a generated id and returns a Location header pointing to it |
| PUT    | /api/rooms/{id}                     | 200, or 400 if capacity is not between 1 and 20, or 404 if missing                                                      | Updates an existing room with new data                                              |
| DELETE | /api/rooms/{id}                     | 204, or 404 if missing, or 409 if the room has reservations                                                             | Deletes a room based on the supplied id                                             |
| GET    | /api/rooms/{roomId}/reservations    | 200, or 404 if the room is missing                                                                                      | Lists the reservations of one room, ordered by start time                           |
| POST   | /api/rooms/{roomId}/reservations    | 201, or 400 if the request is invalid, or 404 if the room is missing, or 409 if the room is already booked at that time | Books a room and returns a Location header pointing to the reservation              |
| GET    | /api/reservations?reservedBy={name} | 200                                                                                                                     | Lists the reservations of one person, with a single `join fetch` query              |
| GET    | /api/reservations/{id}              | 200, or 404 if missing                                                                                                  | Finds a specific reservation                                                        |
| DELETE | /api/reservations/{id}              | 204, or 404 if missing                                                                                                  | Cancels a reservation                                                               |

A room has an id, a name and a capacity. The capacity must be between 1 and 20. POST or PUT request bodies contain only
a name and a capacity because the server assigns the id.  
The API starts with these three rooms:

| ID | Name         | Capacity |
|----|--------------|----------|
| 1  | Seminar A    | 8        |
| 2  | Study Pod    | 4        |
| 3  | Rooftop Room | 12       |

## 409 Conflict

The service layer catches the database's foreign-key error and throws `ConflictException` (mapped to 409).
It is the right layer because the repository should not know about HTTP and the controller should not know about SQL
errors.

## Errors

Every error response has the same `ApiError` body.

| Field       | Meaning                                                      |
|-------------|--------------------------------------------------------------|
| status      | The HTTP status code                                         |
| error       | The reason phrase, such as `Bad Request`                     |
| message     | What went wrong                                              |
| path        | The URL that was requested                                   |
| fieldErrors | A `{ field, message }` for each invalid field, otherwise empty |

Example (api.http request 29, `POST /api/rooms` with capacity 0):

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "request has invalid fields",
  "path": "/api/rooms",
  "fieldErrors": [
    { "field": "capacity", "message": "capacity must be between 1 and 20" }
  ]
}
```

| Status | Meaning                                              |
|--------|------------------------------------------------------|
| 400    | The request itself is wrong: an invalid field, a start that is not before the end, a body that is not valid JSON, a wrong type in the path or a missing parameter |
| 404    | The room, the reservation or the URL does not exist  |
| 409    | The request is valid but clashes with existing data: the room is already booked at that time, or the room still has reservations |
| 500    | A bug on the server. The body only says `unexpected server error` and the details go to the log |

## PUT SQL (api.http request 8)

```text
Hibernate:
select
    r1_0.id,
    r1_0.capacity,
    r1_0.name
from
    room r1_0
where
    r1_0.id=?
Hibernate:
update
    room
set
    capacity=?,
    name=?
where
    id=?
```

PUT loads the room (`select`), changes it, and Hibernate writes the `update` when the transaction commits (dirty checking).

## Database setup

The project uses a MySQL database called roomdb. The app connects as user `roomapp` with password `roomapp1234` (set in
`application.properties`).

MySQL must be installed and running to use this API.
Log in to MySQL as root, in Workbench or the command-line client, and run these three statements:

```sql
CREATE DATABASE roomdb;
CREATE USER 'roomapp'@'localhost' IDENTIFIED BY 'roomapp1234';
GRANT ALL PRIVILEGES ON roomdb.* TO 'roomapp'@'localhost';
```

Then run `reset.sql` from the repository root:  
Command line: `mysql -u roomapp -p roomdb < reset.sql`  
Workbench: open the file and run it.

### Tables

**room**

| Column   | Type         | Rules                                  |
|----------|--------------|----------------------------------------|
| id       | BIGINT       | Primary key, generated by the database |
| name     | VARCHAR(100) | Required                               |
| capacity | INT          | Required, must be between 1 and 20     |

**reservation**

| Column      | Type        | Rules                                  |
|-------------|-------------|----------------------------------------|
| id          | BIGINT      | Primary key, generated by the database |
| room_id     | BIGINT      | Required, foreign key to room.id       |
| reserved_by | VARCHAR(50) | Required                               |
| start_time  | DATETIME    | Required                               |
| end_time    | DATETIME    | Required                               |

The API reads and stores data in MySQL, so it survives a restart.

### Relationship

A room can have 0 to N reservations. The foreign key is `room_id` and sits on `reservation` because a room can have
many reservations, while a reservation has only one room. The room table does not hold a list of reservations because a
list in a single column cannot be joined, checked or deleted one reservation at a time. In Java, `Reservation` has a
`@ManyToOne` field `room` (lazy, joined on `room_id`). `Room` has no list of reservations,
because "the reservations of room 1" is one query method and a two-way link would need both sides kept in step and
could produce endless JSON.

## Overlap rule

Two reservations of one room overlap when `new.start < old.end AND new.end > old.start`. It is implemented as a JPQL
`count` query (`ReservationRepository.countOverlapping`).

Request 19 succeeds because the comparisons are strict (`<` and `>`): a booking from 11:00 to 12:00 starts exactly when
Mina's 10:00 to 11:00 booking ends, so the two only touch and do not overlap.

## Running the project

The API is built on JDK 21. To start it, run RoomApiApplication in IntelliJ or `./gradlew bootRun`.  
The application starts on http://localhost:8080  
api.http requests' expected results will only hold true on fresh data, so reset before testing. You reset the values by
running reset.sql

Documentation:

- Swagger UI: http://localhost:8080/swagger-ui.html
- Raw OpenAPI JSON: http://localhost:8080/v3/api-docs

AI use: When I got stuck, I asked Claude to explain how and why to proceed.