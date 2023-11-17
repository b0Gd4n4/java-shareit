package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.marker.Marker;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */

@Data
@Builder
public class ItemDto {

    @NotNull(groups = Marker.OnUpdate.class)
    private Long id;

    @NotNull(message = "Name cannot be empty or contain spaces.")
    @NotBlank(message = "Name cannot be empty or contain spaces.")
    private String name;

    @NotNull(message = "Description cannot be empty or contain spaces.")
    @NotBlank(message = "Description cannot be empty or contain spaces.")
    private String description;

    @NotNull(message = "Available cannot be empty")
    private Boolean available;

    private BookingShortDto lastBooking;

    private BookingShortDto nextBooking;

    private List<CommentDto> comments;

    @Positive(message = "must be positive")
    @NotNull(groups = Marker.OnUpdate.class)
    private Long requestId;

}