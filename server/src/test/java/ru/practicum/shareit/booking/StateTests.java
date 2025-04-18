package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.ShareItApp;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.exception.UnknownValueException;

import static org.junit.jupiter.api.Assertions.*;

@JsonTest
@ContextConfiguration(classes = ShareItApp.class)
class StateTests {

    @Test
    void getEnumValueValidTesting() {
        assertEquals(State.ALL, State.getEnumValue("ALL"));
        assertEquals(State.CURRENT, State.getEnumValue("CURRENT"));
        assertEquals(State.PAST, State.getEnumValue("PAST"));
        assertEquals(State.FUTURE, State.getEnumValue("FUTURE"));
        assertEquals(State.WAITING, State.getEnumValue("WAITING"));
        assertEquals(State.REJECTED, State.getEnumValue("REJECTED"));
    }

    @Test
    void getEnumValueTesting() {
        assertEquals(State.ALL, State.getEnumValue("all"));
        assertEquals(State.CURRENT, State.getEnumValue("current"));
        assertEquals(State.PAST, State.getEnumValue("past"));
        assertEquals(State.FUTURE, State.getEnumValue("future"));
        assertEquals(State.WAITING, State.getEnumValue("waiting"));
        assertEquals(State.REJECTED, State.getEnumValue("rejected"));
    }

    @Test
    void getEnumValueInvalidTesting() {
        assertThrows(UnknownValueException.class, () -> State.getEnumValue("INVALID"));
        assertThrows(UnknownValueException.class, () -> State.getEnumValue("unknown_state"));
        assertThrows(UnknownValueException.class, () -> State.getEnumValue(""));
        assertThrows(UnknownValueException.class, () -> State.getEnumValue(null));
    }
}

