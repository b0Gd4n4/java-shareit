package item.dto;


import booking.dto.BookingDto;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;



import java.util.List;

@Data
@Builder
public class ItemDto {

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
    private Long requestId;

    private BookingDto lastBooking;

    private BookingDto nextBooking;

    private List<CommentDto> comments;

}
