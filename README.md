The REST API application is for creating, deleting, reading and updating room entries. The list can be filtered by minCapacity (returns rooms with at least that capacity) and/or by keyword (matching full or part of the room name, case is ignored).

| Method | Path | Status | Description |
|--------|------|--------|-------------|
| GET    | /api/rooms | 200 | Finds all rooms |
| GET    | /api/rooms/{id} | 200, or 404 if missing | Finds a specific room |
| POST    | /api/rooms | 201 | Creates a new room with a generated id and returns a Location header pointing to it |
| PUT    | /api/rooms/{id} | 200, or 404 if missing | Updates an existing room with new data |
| DELETE    | /api/rooms/{id} | 204, or 404 if missing | Deletes a room based on the supplied id |

A room has an id, a name and a capacity. POST or PUT request bodies contain only a name and a capacity because the server assigns the id.  
The API starts with these three rooms:

| ID | Name | Capacity |
|----|------|----------|
| 1 | Seminar A | 8 |
| 2 | Study Pod | 4 |
| 3 | Rooftop Room | 12 |

The server only stores the data for as long as it is running. Upon restart all the data would get reset to the starting values.

To run the API either run RoomApiApplication in IntelliJ or ./gradlew bootRun  
The application starts on http://localhost:8080

Documentation:
- Swagger UI: http://localhost:8080/swagger-ui.html
- Raw OpenAPI JSON: http://localhost:8080/v3/api-docs