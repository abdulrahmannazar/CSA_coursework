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