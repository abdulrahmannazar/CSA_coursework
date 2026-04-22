package com.smartcampus.resources;

import com.smartcampus.data.DataStore;
import com.smartcampus.models.Sensor;
import com.smartcampus.models.SensorReading;
import com.smartcampus.exceptions.CustomExceptions.SensorUnavailableException;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.ArrayList;
import java.util.List;

public class SensorReadingResource {
    private final String sensorId;
    private final DataStore store = DataStore.getInstance();

    public SensorReadingResource(String sensorId) { 
        this.sensorId = sensorId;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response add(SensorReading r) {
        Sensor s = store.sensors.get(sensorId);
        
        if (s == null) {
            throw new NotFoundException("Sensor " + sensorId + " not found.");
        }

        // Block readings if sensor is in MAINTENANCE [Task 5]
        if ("MAINTENANCE".equalsIgnoreCase(s.getStatus())) {
            throw new SensorUnavailableException("Forbidden: Sensor " + sensorId + " is currently in maintenance.");
        }
        
        // TASK 4: Side-effect update [Only happens if status is NOT maintenance]
        s.setCurrentValue(r.getValue());
        
        store.readings.computeIfAbsent(sensorId, k -> new ArrayList<>()).add(r);
        
        return Response.status(Response.Status.CREATED).entity(r).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHistory() {
        List<SensorReading> history = store.readings.get(sensorId);
        return Response.ok(history == null ? new ArrayList<>() : history).build();
    }
}