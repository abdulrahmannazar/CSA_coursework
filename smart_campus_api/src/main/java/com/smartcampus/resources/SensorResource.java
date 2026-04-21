package com.smartcampus.resources;
import com.smartcampus.data.DataStore;
import com.smartcampus.models.Sensor;
import com.smartcampus.exceptions.CustomExceptions.LinkedResourceNotFoundException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.util.stream.Collectors;

@Path("/sensors")
public class SensorResource {
    private DataStore store = DataStore.getInstance();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response register(Sensor s) {
        if (!store.rooms.containsKey(s.getRoomId())) {
            throw new LinkedResourceNotFoundException("Invalid Room ID."); // Triggers 422 [cite: 155, 156]
        }
        store.sensors.put(s.getId(), s);
        store.rooms.get(s.getRoomId()).getSensorIds().add(s.getId());
        return Response.status(201).entity(s).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSensors(@QueryParam("type") String type) {
        var list = store.sensors.values().stream();
        if (type != null) list = list.filter(s -> s.getType().equalsIgnoreCase(type));
        return Response.ok(list.collect(Collectors.toList())).build();
    }

    @Path("/{sensorId}/readings")
    public SensorReadingResource getReadings(@PathParam("sensorId") String id) {
        return new SensorReadingResource(id); // Sub-resource locator [cite: 141]
    }
}