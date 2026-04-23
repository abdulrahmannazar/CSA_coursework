package com.smartcampus.exceptions;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.NotFoundException;
import java.util.Map;
import com.smartcampus.exceptions.CustomExceptions.*;

@Provider
public class ExceptionMappers {

    @Provider
    public static class ConflictMapper implements ExceptionMapper<RuntimeException> {
        @Override
        public Response toResponse(RuntimeException e) {
            if (e instanceof RoomAlreadyExistsException || e instanceof RoomNotEmptyException) {
                return Response.status(Response.Status.CONFLICT)
                        .entity(Map.of("error", "Conflict", "message", e.getMessage()))
                        .type(MediaType.APPLICATION_JSON).build();
            }
            if (e instanceof LinkedResourceNotFoundException) {
                return Response.status(422)
                        .entity(Map.of("error", "Unprocessable Entity", "message", e.getMessage()))
                        .type(MediaType.APPLICATION_JSON).build();
            }
            return null; 
        }
    }

    
    @Provider
    public static class ForbiddenMapper implements ExceptionMapper<SensorUnavailableException> {
        @Override
        public Response toResponse(SensorUnavailableException e) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(Map.of("error", "Forbidden", "message", e.getMessage()))
                    .type(MediaType.APPLICATION_JSON).build();
        }
    }

    @Provider
    public static class NotFoundMapper implements ExceptionMapper<NotFoundException> {
        @Override
        public Response toResponse(NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Not Found", "message", e.getMessage()))
                    .type(MediaType.APPLICATION_JSON).build();
        }
    }
    
    
    @Provider
    public static class GlobalSafetyNet implements ExceptionMapper<Throwable> {
        @Override
        public Response toResponse(Throwable e) {
            // Logs the error internally for the developer to see in the console
            e.printStackTrace(); 

            // Returns a professional, sanitized JSON body to the consumer
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of(
                        "error", "Internal Server Error",
                        "message", "A critical system error occurred. Technical details have been hidden for security."
                    ))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }
}