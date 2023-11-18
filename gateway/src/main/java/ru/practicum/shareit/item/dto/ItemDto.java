package ru.practicum.shareit.item.dto;


import ru.practicum.shareit.booking.dto.BookingDto;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.marker.Marker;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;



import java.util.List;

@Data
@Builder
public class ItemDto {

    @NotNull(groups = Marker.OnUpdate.class)
    private Long id;

    @NotNull(message = "Name cannot be empty or contain spaces.")
    @NotBlank(message = "Name cannot be empty or contain spaces.")
    private String name;

    @NotNull(message = "Description cannot be empty")
    @NotBlank(message = "Description cannot be blank")
    private String description;

    @NotNull(message = "Available cannot be empty")
    private Boolean available;

    @Positive(message = "must be positive")
    @NotNull(groups = Marker.OnUpdate.class)
    private Long requestId;

    private BookingDto lastBooking;

    private BookingDto nextBooking;

    private List<CommentDto> comments;

}
