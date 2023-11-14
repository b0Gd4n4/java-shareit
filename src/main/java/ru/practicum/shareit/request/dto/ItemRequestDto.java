package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.marker.Marker;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */

@Data
@Builder
public class ItemRequestDto {

    @NotNull(groups = Marker.OnUpdate.class)
    private long id;

    @NotNull
    @NotBlank
    private String description;

    private LocalDateTime created;

    private List<ItemDto> items;
}
