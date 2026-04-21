package com.smartcampus.exceptions;
import jakarta.ws.rs.core.*;
import jakarta.ws.rs.ext.*;
import java.util.Map;
import com.smartcampus.exceptions.CustomExceptions.*;

@Provider
public class GlobalExceptionMappers {

    @Provider
    public static class GlobalMapper implements ExceptionMapper<Throwable> {
        @Override
        public Response toResponse(Throwable t) {
            return Response.status(500).entity(Map.of("error", "Internal Server Error")).build(); // [cite: 162, 238]
        }
    }

    @Provider
    public static class ConflictMapper implements ExceptionMapper<RoomNotEmptyException> {
        @Override
        public Response toResponse(RoomNotEmptyException e) {
            return Response.status(409).entity(Map.of("error", e.getMessage())).build();
        }
    }
    // Add mappers for 422 and 403 here...
}