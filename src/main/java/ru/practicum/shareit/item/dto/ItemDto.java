package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.marker.Marker;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */

@Data
@Builder
public class ItemDto {

    @NotNull(groups = Marker.OnUpdate.class)
    private long id;

    @NotNull(message = "Name cannot be empty or contain spaces.")
    @NotBlank(message = "Name cannot be empty or contain spaces.")
    private String name;

    @NotNull(message = "Description cannot be empty or contain spaces.")
    @NotBlank(message = "Description cannot be empty or contain spaces.")
    private String description;

    @NotNull(message = "Available cannot be empty")
    private Boolean available;

}