package ru.practicum.shareit.booking.model;

import ru.practicum.shareit.exception.UnknownEnumValueException;

public enum State {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static State getEnumValue(String state) {
        try {
            return State.valueOf(state.toUpperCase());
        } catch (Exception e) {
            throw new UnknownEnumValueException("Unknown state: " + state);
        }
    }
}