package com.smartcampus.resources;

import com.smartcampus.data.DataStore;
import com.smartcampus.models.Sensor;
import com.smartcampus.exceptions.CustomExceptions.LinkedResourceNotFoundException;
import com.smartcampus.exceptions.CustomExceptions.SensorUnavailableException; // Added
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.stream.Collectors;
import java.util.ArrayList;

@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {
    private DataStore store = DataStore.getInstance();

    @POST
    public Response register(Sensor s) {
        if (!store.rooms.containsKey(s.getRoomId())) {
            throw new LinkedResourceNotFoundException("Room ID " + s.getRoomId() + " not found.");
        }
        
        // MITIGATION: Block registration if the status is MAINTENANCE [Task 5]
        if ("MAINTENANCE".equalsIgnoreCase(s.getStatus())) {
            throw new SensorUnavailableException("Forbidden: New sensors cannot be registered in MAINTENANCE status.");
        }
        
        // Ensure new sensors always start at 0.0 for data integrity
        s.setCurrentValue(0.0);
        
        store.sensors.put(s.getId(), s);
        store.rooms.get(s.getRoomId()).getSensorIds().add(s.getId());
        
        return Response.status(201).entity(s).build();
    }

    @GET
    public Response get(@QueryParam("type") String type) {
        var res = store.sensors.values().stream();
        if (type != null && !type.isEmpty()) {
            res = res.filter(s -> s.getType().equalsIgnoreCase(type));
        }
        return Response.ok(res.collect(Collectors.toList())).build();
    }

    @GET
    @Path("/{id}")
    public Response getOne(@PathParam("id") String id) {
        Sensor s = store.sensors.get(id);
        if (s == null) {
            throw new NotFoundException("Sensor " + id + " does not exist.");
        }
        return Response.ok(s).build();
    }

    @Path("/{sensorId}/readings")
    public SensorReadingResource getReadings(@PathParam("sensorId") String id) {
        if (!store.sensors.containsKey(id)) {
            throw new NotFoundException("Cannot access readings: Sensor " + id + " not found.");
        }
        return new SensorReadingResource(id);
    }
}