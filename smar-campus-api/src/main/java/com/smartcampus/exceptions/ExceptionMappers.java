package com.smartcampus.exceptions;


import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.Map;
import com.smartcampus.exceptions.CustomExceptions.*;

@Provider
public class ExceptionMappers {

    // Mapper for 409 Conflict [cite: 153]
    @Provider
    public static class ConflictMapper implements ExceptionMapper<RoomNotEmptyException> {
        @Override
        public Response toResponse(RoomNotEmptyException e) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(Map.of("error", "Conflict", "message", e.getMessage()))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }

    // Mapper for 422 Unprocessable Entity [cite: 156, 157]
    @Provider
    public static class UnprocessableMapper implements ExceptionMapper<LinkedResourceNotFoundException> {
        @Override
        public Response toResponse(LinkedResourceNotFoundException e) {
            return Response.status(422)
                    .entity(Map.of("error", "Unprocessable Entity", "message", e.getMessage()))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }

    // Mapper for 403 Forbidden [cite: 160]
    @Provider
    public static class ForbiddenMapper implements ExceptionMapper<SensorUnavailableException> {
        @Override
        public Response toResponse(SensorUnavailableException e) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(Map.of("error", "Forbidden", "message", e.getMessage()))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }
}