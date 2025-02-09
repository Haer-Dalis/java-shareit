package ru.practicum.shareit.exception;

public class ConflictException extends IllegalStateException {
    public ConflictException(String message) {
        super(message);
    }
}