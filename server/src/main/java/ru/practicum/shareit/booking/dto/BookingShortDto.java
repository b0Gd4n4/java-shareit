package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

@Data
@Builder
public class BookingShortDto {
    private Long id;
    private Long bookerId;
    @FutureOrPresent
    @Future
    private LocalDateTime start;
    @FutureOrPresent
    @Future
    private LocalDateTime end;
}