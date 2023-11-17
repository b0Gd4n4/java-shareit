package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.marker.Marker;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */

@Data
@Builder
public class BookingDto {
    @NotNull(groups = Marker.OnUpdate.class)
    private Long itemId;

    @NotNull(message = "start cannot be empty.")
    @FutureOrPresent(message = "start may be in the present or future")
    private LocalDateTime start;

    @NotNull(message = "end cannot be empty.")
    @Future(message = "end may be in the future")
    private LocalDateTime end;

    private BookingStatus status;
}