package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.StatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class StateTest {
    @Test
    void getEnumValue() {

        String stateStr = "Unknown";
        String finalStateStr = stateStr;
        assertThrows(StatusException.class, () -> BookingState.getEnumValue(finalStateStr));

        stateStr = "ALL";
        BookingState stateTest = BookingState.getEnumValue(stateStr);
        assertEquals(stateTest, BookingState.ALL);

        stateStr = "CURRENT";
        stateTest = BookingState.getEnumValue(stateStr);
        assertEquals(stateTest, BookingState.CURRENT);

        stateStr = "PAST";
        stateTest = BookingState.getEnumValue(stateStr);
        assertEquals(stateTest, BookingState.PAST);

        stateStr = "FUTURE";
        stateTest = BookingState.getEnumValue(stateStr);
        assertEquals(stateTest, BookingState.FUTURE);

        stateStr = "REJECTED";
        stateTest = BookingState.getEnumValue(stateStr);
        assertEquals(stateTest, BookingState.REJECTED);

        stateStr = "WAITING";
        stateTest = BookingState.getEnumValue(stateStr);
        assertEquals(stateTest, BookingState.WAITING);
    }
}
