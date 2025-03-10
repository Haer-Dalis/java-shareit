package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.ShareItApp;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
@ContextConfiguration(classes = ShareItApp.class)
public class ErrorHandlerTest {

    private final ErrorHandler handler = new ErrorHandler();

    @Test
    public void testHandleNotFoundException() {
        NotFoundException ex = new NotFoundException("Item not found");
        ErrorResponse response = handler.handleNotFound(ex);

        assertEquals("Item not found", response.getError());
    }

    @Test
    public void testHandleValidationException() {
        ValidationException ex = new ValidationException("Invalid input");
        ErrorResponse response = handler.handleNotValid(ex);

        assertEquals("Invalid input", response.getError());
    }

    @Test
    public void testHandleAccessDeniedException() {
        AccessDeniedException ex = new AccessDeniedException("Access denied");
        ErrorResponse response = handler.handleNotValid(ex);

        assertEquals("Access denied", response.getError());
    }

    @Test
    public void testHandleConflictException() {
        ConflictException ex = new ConflictException("Conflict occurred");
        ErrorResponse response = handler.handleConflictError(ex);

        assertEquals("Conflict occurred", response.getError());
    }
}