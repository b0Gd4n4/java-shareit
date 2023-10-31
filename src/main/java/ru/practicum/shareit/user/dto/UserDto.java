package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.marker.Marker;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */

@Data
@Builder
public class UserDto {

    @NotNull(groups = Marker.OnUpdate.class)
    private long id;

    @NotNull(message = "Login cannot be empty or contain spaces.")
    @NotBlank(message = "Login cannot be empty or contain spaces.")
    private String name;

    @NotNull(message = "Email cannot be empty")
    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Email must contain the character @")
    private String email;
}