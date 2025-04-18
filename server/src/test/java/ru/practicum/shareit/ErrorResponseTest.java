package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.ErrorResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ErrorResponseTest {

    private ErrorResponse errorResponse;
    private String error = "textError";

    @Test
    void errorResponse() {
        errorResponse = new ErrorResponse(error);
        assertEquals(error, errorResponse.getError());
    }

}
