package user.dto;

import lombok.Builder;
import lombok.Data;
import marker.Marker;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class UserDto {

    @NotNull(groups = Marker.OnUpdate.class)
    private Long id;

    private String name;

    @NotEmpty
    @Email
    private String email;

}