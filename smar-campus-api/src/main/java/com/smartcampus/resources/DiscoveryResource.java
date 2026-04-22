package com.smartcampus.resources;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.util.Map;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class DiscoveryResource {
    @GET
    public Response getDiscovery() {
        return Response.ok(Map.of(
                "name", "Smart Campus API",
                "version", "v1",
                "admin_contact", "student@westminster.ac.uk",
                "resources", Map.of("rooms", "/api/v1/rooms", "sensors", "/api/v1/sensors")
        )).build();
    }
}