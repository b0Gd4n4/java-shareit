package request.dto;

import item.dto.ItemDto;
import lombok.Builder;
import lombok.Data;
import marker.Marker;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ItemRequestDto {

    @NotNull(groups = Marker.OnUpdate.class)
    private long id;

    @NotNull(message = "Description cannot be empty")
    @NotBlank(message = "Description cannot be blank")
    @NotEmpty
    private String description;

    private LocalDateTime created;

    private List<ItemDto> items;
}