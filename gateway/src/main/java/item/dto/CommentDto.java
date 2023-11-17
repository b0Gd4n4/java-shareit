package item.dto;

import lombok.Builder;
import lombok.Data;
import marker.Marker;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
public class CommentDto {

    @NotNull(groups = Marker.OnUpdate.class)
    public Long id;

    @NotNull(message = "text cannot be empty.")
    @NotBlank(message = "text cannot be empty.")
    private String text;

    private LocalDateTime created;

    private String authorName;
}
