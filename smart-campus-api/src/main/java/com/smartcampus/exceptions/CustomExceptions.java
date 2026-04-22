package com.smartcampus.exceptions;

public class CustomExceptions {
    public static class RoomNotEmptyException extends RuntimeException {
        public RoomNotEmptyException(String msg) { super(msg); }
    }
    public static class LinkedResourceNotFoundException extends RuntimeException {
        public LinkedResourceNotFoundException(String msg) { super(msg); }
    }
    public static class SensorUnavailableException extends RuntimeException {
        public SensorUnavailableException(String msg) { super(msg); }
    }
}