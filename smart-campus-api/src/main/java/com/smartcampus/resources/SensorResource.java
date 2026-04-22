package com.smartcampus.resources;
import com.smartcampus.data.DataStore;
import com.smartcampus.models.Sensor;
import com.smartcampus.exceptions.CustomExceptions.LinkedResourceNotFoundException;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.stream.Collectors;

@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
public class SensorResource {
    private DataStore store = DataStore.getInstance();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response register(Sensor s) {
        if (!store.rooms.containsKey(s.getRoomId())) {
            throw new LinkedResourceNotFoundException("Room ID not found."); // cite: 155
        }
        store.sensors.put(s.getId(), s);
        store.rooms.get(s.getRoomId()).getSensorIds().add(s.getId());
        return Response.status(201).entity(s).build();
    }

    @GET
    public Response get(@QueryParam("type") String type) {
        var res = store.sensors.values().stream();
        if (type != null) res = res.filter(s -> s.getType().equalsIgnoreCase(type));
        return Response.ok(res.collect(Collectors.toList())).build();
    }

    @Path("/{sensorId}/readings")
    public SensorReadingResource getReadings(@PathParam("sensorId") String id) {
        return new SensorReadingResource(id); // cite: 141
    }
}