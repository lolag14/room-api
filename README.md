The REST API application is for creating, deleting, reading and updating room entries.

| Method | Path | Status | Description |
|--------|------|--------|-------------|
| GET    | /api/rooms | 200 | Finds all rooms |
| GET    | /api/rooms/{id} | 200, or 404 if missing | Finds a specific room |
| POST    | /api/rooms | 201 | Creates a new room with a generated id and returns a Location header pointing to it |
| PUT    | /api/rooms/{id} | 200, or 404 if missing | Updates an existing room with new data |
| DELETE    | /api/rooms/{id} | 204 | Deletes a room based on the supplied id |

To run the API either run RoomApiApplication in IntelliJ or ./gradlew bootRun  
The application starts on http://localhost:8080