package com.smartcampus.resources;
import com.smartcampus.data.DataStore;
import com.smartcampus.models.SensorReading;
import com.smartcampus.exceptions.CustomExceptions.SensorUnavailableException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.util.ArrayList;

public class SensorReadingResource {
    private String sensorId;
    private DataStore store = DataStore.getInstance();

    public SensorReadingResource(String sensorId) { this.sensorId = sensorId; }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response add(SensorReading r) {
        var s = store.sensors.get(sensorId);
        if ("MAINTENANCE".equals(s.getStatus())) throw new SensorUnavailableException("Sensor offline."); // cite: 160

        s.setCurrentValue(r.getValue()); // cite: 146
        store.readings.computeIfAbsent(sensorId, k -> new ArrayList<>()).add(r);
        return Response.status(201).entity(r).build();
    }
}