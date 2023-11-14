package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;


@Data
@Builder
public class BookingOutDto {

    private Long id;

    @FutureOrPresent
    @Future
    private LocalDateTime start;
    @FutureOrPresent
    @Future
    private LocalDateTime end;

    private BookingStatus status;

    private UserDto booker;

    private ItemDto item;
}
