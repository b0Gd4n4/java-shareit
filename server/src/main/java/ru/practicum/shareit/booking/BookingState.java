package ru.practicum.shareit.booking;

import ru.practicum.shareit.exception.StatusException;

public enum BookingState {

    ALL,

    CURRENT,

    PAST,

    FUTURE,

    WAITING,

    REJECTED;

    public static BookingState getEnumValue(String state) {

        try {
            return BookingState.valueOf(state);
        } catch (Exception e) {
            throw new StatusException("Unknown state: " + state);
        }

    }
}