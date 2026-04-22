package com.smartcampus.resources;

import javax.ws.rs.*; // Changed from jakarta
import javax.ws.rs.core.*;
import java.util.Map;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class DiscoveryResource {
    @GET
    public Response getDiscovery() {
        // Implementation of root "Discovery" endpoint [cite: 109]
        return Response.ok(Map.of(
            "version", "1.0",
            "contact", "admin@westminster.ac.uk",
            "collections", Map.of("rooms", "/api/v1/rooms", "sensors", "/api/v1/sensors")
        )).build();
    }
}