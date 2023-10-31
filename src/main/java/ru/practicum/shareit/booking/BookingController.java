package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;




/**
 * TODO Sprint add-bookings.
 */

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingOutDto createBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @RequestBody @Valid BookingDto bookingDto) {

        log.info("User {}, add new booking {}", userId, "bookingDto.getName()");
        return bookingService.createBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingOutDto approveBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                        @PathVariable Long bookingId,
                                        @RequestParam Boolean approved) {

        log.info("User {}, changed the status booking {}", userId, bookingId);
        return bookingService.approveBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingOutDto getBookingById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                        @PathVariable Long bookingId) {

        log.info("Get booking {}", bookingId);
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    public List<BookingOutDto> getAllBookingsByBookerId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                        @RequestParam(defaultValue = "ALL", required = false) String state) {

        log.info("Get all bookings by booker Id {}", userId);
        return bookingService.getAllBookingsByBookerId(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingOutDto> getAllBookingsForAllItemsByOwnerId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                                  @RequestParam(defaultValue = "ALL", required = false) String state) {

        log.info("Get all bookings for all items by owner Id {}", userId);
        return bookingService.getAllBookingsForAllItemsByOwnerId(userId, state);
    }
}