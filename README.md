# CSA_coursework



# 🌐 Project Overview
The **Smart Campus Sensor & Room Management API** is a specialized RESTful web service designed to orchestrate the Internet of Things (IoT) ecosystem within a modern university campus. As academic institutions transition into "Smart Campuses," the ability to monitor environmental conditions and room occupancy in real-time becomes critical.

This API serves as the central middleware for facility managers, allowing them to track physical assets (Rooms) and the telemetry devices (Sensors) attached to them. By providing a unified interface for data ingestion and retrieval, the system enables automated energy management, security monitoring, and efficient space utilization.

### Technical Motivation: The REST Advantage
Unlike traditional RPC or SOAP-based services, this implementation utilizes **REST (Representational State Transfer)** for three primary reasons:
1.  **Statelessness:** Each request contains all the information necessary for processing, reducing server-side overhead and simplifying the horizontal scaling of campus infrastructure.
2.  **Scalability:** The decoupling of the client and server allows the university to update frontend monitoring dashboards or physical sensor hardware independently.
3.  **Uniform Interface:** By leveraging standard HTTP methods (GET, POST, DELETE), the API remains intuitive and easy to integrate with various third-party IoT platforms.

### Richardson Maturity Model
This implementation reaches **Level 3 (Hypermedia Controls)**. While it utilizes standard URIs (Level 1) and HTTP Verbs (Level 2), it specifically incorporates **HATEOAS** (Hypermedia as the Engine of Application State) within the Discovery endpoint. By providing navigable links in the root response, the API guides the client through the available resource collections dynamically.



---

# 🏛️ Architectural Framework
The system follows a decoupled, layered architecture to ensure maintainability and security.

* **Transport & Routing Layer:** Managed by **Jersey (JAX-RS)** and **Apache Tomcat**. The `web.xml` and `AppConfig.java` act as the entry points, routing incoming HTTP traffic based on URI patterns.
* **Resource Layer:** Implemented via classes like `SensorRoomResource` and `SensorResource`. This layer handles the translation of HTTP requests into logic calls and manages the **Sub-resource Locator** pattern for sensor readings.
* **Service Layer (Business Logic):** Embedded within the resource methods, this layer enforces data integrity—such as the "Safety Logic" that prevents the deletion of occupied rooms.
* **Representation Layer:** Uses the **Jackson JSON provider** to automatically serialize and deserialize Java POJOs (Models) into standard JSON formats.

### Design Patterns
* **Singleton Pattern:** The `DataStore` class ensures a single, consistent instance of the in-memory database exists across the application lifecycle.
* **Thread Safety:** Because JAX-RS resources are concurrent by nature, I utilized `ConcurrentHashMap` and `Collections.synchronizedList` to prevent data corruption during simultaneous read/write operations.

---

# 🚀 Getting Started

### Prerequisites
* **Java Version:** JDK 17 or higher.
* **Build Tool:** Apache Maven 3.9+.
* **Server:** Apache Tomcat 9.x (Compatible with the `javax` namespace implementation).

### Installation & Execution
1.  **Build:** In the root directory, execute:
    ```bash
    mvn clean package
    ```
2.  **Deploy:** Copy the resulting `target/smart-campus-api.war` to your Tomcat `/webapps` folder.
3.  **Launch:** Start the server via `bin/startup.bat`.

### Base URL
The API is accessible at:
`http://localhost:8080/smart-campus-api/api/v1`

---

# 🛠️ API Interaction Examples

### 1. Discovery (Root) Endpoint
`GET /`
```bash
curl -X GET http://localhost:8080/smart-campus-api/api/v1/
```

### 2. Room Management
**Create a Room:**
```bash
curl -X POST http://localhost:8080/smart-campus-api/api/v1/rooms \
-H "Content-Type: application/json" \
-d '{"id": "LIB-301", "name": "Quiet Study", "capacity": 45}'
```

**List All Rooms:**
```bash
curl -X GET http://localhost:8080/smart-campus-api/api/v1/rooms
```

### 3. Sensor Management
**Register Sensor to Room:**
```bash
curl -X POST http://localhost:8080/smart-campus-api/api/v1/sensors \
-H "Content-Type: application/json" \
-d '{"id": "TEMP-01", "type": "Temperature", "roomId": "LIB-301"}'
```

**Filter Sensors by Type:**
```bash
curl -X GET "http://localhost:8080/smart-campus-api/api/v1/sensors?type=Temperature"
```

### 4. Sensor Readings (Nested Resource)
**Submit a New Reading:**
```bash
curl -X POST http://localhost:8080/smart-campus-api/api/v1/sensors/TEMP-01/readings \
-H "Content-Type: application/json" \
-d '{"id": "R1", "value": 22.5, "timestamp": 1713800000}'
```

---

# 📝 Conceptual Report (Reflection Answers)

* **JAX-RS Lifecycle:** JAX-RS resources are request-scoped; a new instance is created per request. My `DataStore` is a **Singleton** to ensure data persists across these short-lived instances.
* **HATEOAS Benefits:** It makes the API self-documenting. A client only needs the root URL to discover all other endpoints, reducing the coupling between client knowledge and server structure.
* **IDs vs Full Objects:** Returning only IDs in the Room model (`sensorIds`) reduces the payload size and network bandwidth. However, full objects reduce "chattiness" by preventing the client from making multiple extra calls.
* **DELETE Idempotency:** My `DELETE` method is idempotent because the first call removes the room; subsequent identical calls do not change the server state (the room remains gone).
* **JSON Mismatch:** If a client sends non-JSON data, the `@Consumes(MediaType.APPLICATION_JSON)` annotation triggers a **415 Unsupported Media Type** error automatically.
* **Query vs Path Filtering:** I use **Path Parameters** for resource identity (unique IDs) and **Query Parameters** for non-identifying logic like filtering sensors by type.
* **Sub-resource Locator Benefits:** This pattern allows the `SensorReadingResource` to be managed as a child of a specific Sensor, making the code modular and the URI hierarchy logical.
* **Side-Effects:** When a Reading is POSTed, the parent Sensor's `currentValue` is updated. This ensures **Data Consistency**, so a GET on the Sensor always reflects the latest telemetry.
* **422 vs 404:** I return **422 Unprocessable Entity** when a Sensor references a non-existent Room. This distinguishes between a bad URL (404) and a logically invalid data relationship (422).
* **Cybersecurity & Stack Traces:** Exposing stack traces reveals internal class names and library versions. Attackers use this "Information Disclosure" to find specific vulnerabilities in your tech stack.
* **Filters vs Manual Logging:** Using a `ContainerRequestFilter` ensures **Cross-Cutting Concerns** (logging) are handled in one place, keeping resource classes focused on business logic.

---

# 🏛️ Technical Quality & Security Analysis
* **Leak-Proof Exception Mapping:** I implemented `ExceptionMapper<Throwable>` to intercept every possible error. This ensures the API always returns a clean JSON error body rather than a default Tomcat HTML error page.
* **Data Integrity:** The `DELETE` logic in `SensorRoomResource` includes a safety check that prevents a Room from being deleted if its `sensorIds` list is not empty, preventing orphaned sensors.
* **Thread Safety:** By using `ConcurrentHashMap` in the `DataStore`, the API handles multiple concurrent Postman requests safely without locking or data loss.

---

# 📁 Project Structure
* `com.smartcampus.models`: POJO entities (Room, Sensor, Reading).
* `com.smartcampus.resources`: JAX-RS endpoints and sub-resource locators.
* `com.smartcampus.data`: Singleton in-memory storage logic.
* `com.smartcampus.exceptions`: Custom exception classes and JSON response mappers.
* `com.smartcampus.filters`: Logging filters for request/response observability.
* `com.smartcampus.config`: Versioned API configuration (`AppConfig`).
* `webapp/WEB-INF`: Deployment descriptor (`web.xml`).