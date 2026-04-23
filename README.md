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

🛡️ Task 5.2: Global Safety Net & Cybersecurity Mitigation
I have implemented a catch-all ExceptionMapper<Throwable> within ExceptionMappers.java to act as a global safety net for the Smart Campus API. This implementation is critical for preventing Information Disclosure, a common cybersecurity vulnerability.

Risk Mitigation: By default, unhandled exceptions in a Java environment like Tomcat 9 can leak raw stack traces to the end-user. These traces are dangerous because they expose internal file paths, specific library versions (such as Jersey or Jackson), and the underlying business logic flows to potential attackers.

Reconnaissance Defense: Attackers often use these technical details during the "reconnaissance" phase of an exploit to find known vulnerabilities in specific software versions. My implementation intercepts every possible Throwable and returns a sanitized 500 Internal Server Error in a structured JSON format.

Implementation Detail: This ensures that even if a critical code failure occurs, the consumer only sees a generic error message, while the actual technical error is hidden to protect the integrity of the campus infrastructure.

---
# Report Q & A


Question 1 

How does the JAX-RS resource lifecycle affect data persistence, and how is synchronization managed 
for in-memory data? 

By default, JAX-RS resources are request-scoped, meaning the container creates a new instance for every 
incoming HTTP request. Since this project uses in-memory data structures rather than a database, the 
DataStore must be implemented as a Singleton to ensure data persists across multiple requests. To handle 
concurrent access from multiple clients, I utilized thread-safe collections like ConcurrentHashMap to 
synchronize data operations and prevent race conditions or data corruption. 


Question 2  

What are the specific architectural benefits of using HATEOAS for a discovery endpoint? 

Implementing HATEOAS (Hypermedia as the Engine of Application State) in the discovery endpoint 
(/api/v1/) makes the API self-documenting. This decouples the client from the server’s URI structure, 
allowing the university to change resource paths without breaking the client application, as the client 
dynamically discovers the current locations of the "rooms" and "sensors" collections via the provided links. 


Question 3  

Why is it advantageous to return a list of IDs rather than full objects in a resource collection 
response? 

In the Room resource, returning a list of Sensor IDs instead of full Sensor objects significantly reduces the 
payload size and network bandwidth consumption. While returning full objects reduces "chattiness" by 
preventing extra round-trips, returning IDs is more scalable for a campus-wide IoT infrastructure where a 
single room might eventually host hundreds of devices. 
   

Question 4  

Explain the concept of idempotency in the context of deleting resources. 

The DELETE operation is idempotent because repeat requests result in the same final state on the server: 
the resource is gone. Whether the server returns a 204 No Content for the first successful deletion or a 404 
Not Found for subsequent attempts, the resulting side-effect (the non-existence of that resource) remains 
identical. 


Question 5  

How does the API handle and validate invalid media types during request processing? 

Using the @Consumes(MediaType.APPLICATION_JSON) annotation, the API explicitly restricts the 
types of data it will accept. If a client attempts to send an unsupported format, such as XML or plain text, 
the JAX-RS container automatically generates a 415 Unsupported Media Type response, protecting the 
server from processing malformed data. 


Question 6  

When should Path Parameters be used instead of Query Parameters in a RESTful API? 

Path Parameters are used for resource identification, such as targeting a specific unique Sensor ID. In 
contrast, Query Parameters are used for non-identifying logic like filtering, sorting, or pagination—for 
example, filtering a list of sensors by their type (e.g., ?type=CO2). 


Question 7  

What are the advantages of using the Sub-resource Locator pattern for nested resources? 

Implementing /readings as a sub-resource locator within the Sensor resource creates a logical, modular 
hierarchy. This ensures that telemetry data cannot exist in isolation and must always be accessed through 
its parent sensor, making the API structure intuitive for monitoring specific hardware. 


Question 8 

How does the API ensure data consistency when performing operations that have side-effects? 

When a new reading is POST to the telemetry endpoint, the parent Sensor’s currentValue is updated as an 
automatic side-effect. This maintains data consistency by ensuring that any subsequent GET request for 
that Sensor resource immediately reflects the most recent telemetry value without requiring manual data 
aggregation. 


Question 9 

Why is a 422 Unprocessable Entity status code semantically superior to 404 for payload reference 
issues? 

A 422 Unprocessable Entity is used when a sensor registration fails because the provided Room ID does 
not exist. This is semantically superior to a 404 Not Found because the endpoint URL itself is valid, but the 
data within the request body contains a logical error (a bad reference), which helps developers distinguish 
between routing issues and data integrity issues. 
   

Question 10 

What are the cybersecurity risks associated with exposing raw Java stack traces to API consumers? 

Exposing stack traces is a significant "Information Disclosure" risk that reveals internal server directory 
structures, specific library versions, and logic flows to potential attackers. This information can be used for 
reconnaissance to find specific vulnerabilities in the server's environment; therefore, a "Global Safety Net" 
using an ExceptionMapper<Throwable> is implemented to hide these technical details and return a clean 
JSON error instead. 


Question 11  

Why is it better to use a Container Filter for logging instead of manual logging within each resource? 

Using a ContainerRequestFilter and ContainerResponseFilter handles "Cross-Cutting Concerns" centrally. 
This ensures that every request and response is consistently logged for auditing and observability without 
cluttering the business logic in the resource classes, making the codebase cleaner and easier to maintain. 

---

# 📁 Project Structure
* `com.smartcampus.models`: POJO entities (Room, Sensor, Reading).
* `com.smartcampus.resources`: JAX-RS endpoints and sub-resource locators.
* `com.smartcampus.data`: Singleton in-memory storage logic.
* `com.smartcampus.exceptions`: Custom exception classes and JSON response mappers.
* `com.smartcampus.filters`: Logging filters for request/response observability.
* `com.smartcampus.config`: Versioned API configuration (`AppConfig`).
* `webapp/WEB-INF`: Deployment descriptor (`web.xml`).