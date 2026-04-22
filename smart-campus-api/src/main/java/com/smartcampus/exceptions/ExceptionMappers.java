package com.smartcampus.exceptions;

import javax.ws.rs.core.MediaType; // Changed from jakarta
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.Map;
import com.smartcampus.exceptions.CustomExceptions.*;

@Provider
public class ExceptionMappers {

    @Provider
    public static class ConflictMapper implements ExceptionMapper<RoomNotEmptyException> {
        @Override
        public Response toResponse(RoomNotEmptyException e) {
            // Task: HTTP 409 Conflict with JSON body [cite: 153]
            return Response.status(Response.Status.CONFLICT)
                    .entity(Map.of("error", e.getMessage()))
                    .type(MediaType.APPLICATION_JSON).build();
        }
    }

    @Provider
    public static class GlobalMapper implements ExceptionMapper<Throwable> {
        @Override
        public Response toResponse(Throwable t) {
            // Task: Intercept unexpected errors and return generic HTTP 500 [cite: 162]
            return Response.status(500).entity(Map.of("error", "Internal Error")).build();
        }
    }
}