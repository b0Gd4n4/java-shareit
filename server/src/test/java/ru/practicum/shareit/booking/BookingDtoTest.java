package ru.practicum.shareit.booking;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class BookingDtoTest {

    @Autowired
    private JacksonTester<BookingOutDto> json;

    @Test
    void testBookingDto() throws Exception {

        LocalDateTime start = LocalDateTime.of(2023, 11, 19, 16, 0);
        LocalDateTime end = LocalDateTime.of(2023, 11, 19, 20, 0);

        UserDto user = UserDto.builder()
                .id(1L)
                .name("Name")
                .email("name@yandex.ru")
                .build();

        ItemDto itemDto = ItemDto.builder()
                .id(2L)
                .name("fdhehjgdeh")
                .description("new name")
                .available(true)
                .build();

        BookingOutDto bookingOutDto = BookingOutDto.builder()
                .id(1L)
                .start(start)
                .end(end)
                .status(BookingStatus.APPROVED)
                .booker(user)
                .item(itemDto)
                .build();

        JsonContent<BookingOutDto> result = json.write(bookingOutDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("APPROVED");
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo("Name");
        assertThat(result).extractingJsonPathStringValue("$.booker.email").isEqualTo("name@yandex.ru");
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("fdhehjgdeh");
        assertThat(result).extractingJsonPathBooleanValue("$.item.available").isEqualTo(true);
    }
}