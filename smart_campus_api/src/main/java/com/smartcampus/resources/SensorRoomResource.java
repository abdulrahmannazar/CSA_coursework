package com.smartcampus.resources;
import com.smartcampus.data.DataStore;
import com.smartcampus.models.Room;
import com.smartcampus.exceptions.CustomExceptions.RoomNotEmptyException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.util.ArrayList;

@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorRoomResource {
    private DataStore store = DataStore.getInstance();

    @POST
    public Response createRoom(Room r) {
        store.rooms.put(r.getId(), r);
        return Response.status(201).entity(r).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteRoom(@PathParam("id") String id) {
        Room r = store.rooms.get(id);
        if (r != null && !r.getSensorIds().isEmpty()) {
            throw new RoomNotEmptyException("Cannot delete: Room has active sensors."); // Triggers 409 [cite: 151, 153]
        }
        store.rooms.remove(id);
        return Response.noContent().build();
    }
}