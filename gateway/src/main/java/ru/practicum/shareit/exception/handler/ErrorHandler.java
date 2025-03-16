package ru.practicum.shareit.exception.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.UnknownEnumValueException;
import ru.practicum.shareit.exception.UnsupportedStatusException;

import java.util.stream.Collectors;

@RestControllerAdvice
public class ErrorHandler {

    private static final Logger log = LoggerFactory.getLogger(ErrorHandler.class);

    @ExceptionHandler(UnsupportedStatusException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleUnsupportedStatusException(final UnsupportedStatusException exception) {
        log.error("Ошибка UnsupportedStatusException: {}", exception.getMessage(), exception);
        return new ErrorResponse(exception.getMessage());
    }

    @ExceptionHandler(UnknownEnumValueException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleEnumValueException(final UnknownEnumValueException exception) {
        log.error("Ошибка UnknownEnumValueException: {}", exception.getMessage(), exception);
        return new ErrorResponse(exception.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleAllExceptions(final Exception exception) {
        log.error("Необработанная ошибка: ", exception);
        return new ErrorResponse("Внутренняя ошибка сервера: " + exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationExceptions(MethodArgumentNotValidException exception) {
        String errorMessage = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        log.error("Ошибка валидации: {}", errorMessage);
        return new ErrorResponse("Ошибка валидации: " + errorMessage);
    }
}
