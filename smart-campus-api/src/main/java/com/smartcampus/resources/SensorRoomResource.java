package com.smartcampus.resources;
import com.smartcampus.data.DataStore;
import com.smartcampus.models.Room;
import com.smartcampus.exceptions.CustomExceptions.RoomNotEmptyException;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.ArrayList;

@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorRoomResource {
    private DataStore store = DataStore.getInstance();

    @GET
    public Response getAll() { return Response.ok(new ArrayList<>(store.rooms.values())).build(); }

    @POST
    public Response create(Room r) {
        store.rooms.put(r.getId(), r);
        return Response.status(201).entity(r).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") String id) {
        Room r = store.rooms.get(id);
        if (r != null && !r.getSensorIds().isEmpty()) {
            throw new RoomNotEmptyException("Cannot delete: Room LIB-301 has active sensors."); // cite: 153
        }
        store.rooms.remove(id);
        return Response.noContent().build();
    }
}