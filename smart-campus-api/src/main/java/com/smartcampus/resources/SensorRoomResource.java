package com.smartcampus.resources;

import com.smartcampus.data.DataStore;
import com.smartcampus.models.Room;
import com.smartcampus.exceptions.CustomExceptions.RoomNotEmptyException;
import com.smartcampus.exceptions.CustomExceptions.RoomAlreadyExistsException; 
import com.smartcampus.exceptions.CustomExceptions.SensorUnavailableException;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.ArrayList;

@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorRoomResource {
    private DataStore store = DataStore.getInstance();

    @GET
    public Response getAll() { 
        return Response.ok(new ArrayList<>(store.rooms.values())).build(); 
    }

    // New: Individual Room Lookup (Required for your test cases)
    @GET
    @Path("/{id}")
    public Response getOne(@PathParam("id") String id) {
        Room r = store.rooms.get(id);
        if (r == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(r).build();
    }

    @POST
    public Response create(Room r) {
        // TASK 2.1: Check if Room ID already exists
        if (store.rooms.containsKey(r.getId())) {
            throw new RoomAlreadyExistsException("Conflict: Room " + r.getId() + " is already registered.");
        }
        
        store.rooms.put(r.getId(), r);
        return Response.status(201).entity(r).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") String id) {
        Room r = store.rooms.get(id);
        
        // Return 404 if room doesn't exist
        if (r == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        // TASK 2.4: Safety Logic - Dynamic check for sensors
        if (!r.getSensorIds().isEmpty()) {
            throw new RoomNotEmptyException("Cannot delete: Room " + id + " has active sensors."); 
        }

        store.rooms.remove(id);
        return Response.noContent().build();
    }
}