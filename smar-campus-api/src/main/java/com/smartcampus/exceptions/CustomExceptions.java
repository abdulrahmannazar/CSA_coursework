package com.smartcampus.exceptions;

public class CustomExceptions {

    // This is the error for Part 5, Task 1 [cite: 153]
    public static class RoomNotEmptyException extends RuntimeException {
        public RoomNotEmptyException(String message) {
            super(message);
        }
    }

    // This is the error for Part 5, Task 2 [cite: 155]
    public static class LinkedResourceNotFoundException extends RuntimeException {
        public LinkedResourceNotFoundException(String message) {
            super(message);
        }
    }

    // This is the error for Part 5, Task 3 [cite: 160]
    public static class SensorUnavailableException extends RuntimeException {
        public SensorUnavailableException(String message) {
            super(message);
        }
    }
}