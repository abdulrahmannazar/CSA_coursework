package com.smartcampus.exceptions;

public class CustomExceptions {
    
    public static class RoomAlreadyExistsException extends RuntimeException {
        public RoomAlreadyExistsException(String message) { super(message); }
    }

    public static class RoomNotEmptyException extends RuntimeException {
        public RoomNotEmptyException(String message) { super(message); }
    }

    public static class LinkedResourceNotFoundException extends RuntimeException {
        public LinkedResourceNotFoundException(String message) { super(message); }
    }
    public static class SensorUnavailableException extends RuntimeException {
    public SensorUnavailableException(String message) {
        super(message);
    }
}
}