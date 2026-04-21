package com.smartcampus.resources;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.util.Map;

@Path("/")
public class DiscoveryResource {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDiscovery() {
        return Response.ok(Map.of(
                "version", "1.0",
                "contact", "admin@westminster.ac.uk",
                "resources", Map.of("rooms", "/api/v1/rooms", "sensors", "/api/v1/sensors")
        )).build();
    }
}