package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.check.CheckService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final CheckService checkService;

    @Transactional
    @Override
    public BookingOutDto createBooking(BookingDto bookingDto, long userId) {

        checkService.checkItem(bookingDto.getItemId());
        Item item = itemRepository.findById(bookingDto.getItemId()).get();

        checkService.checkUser(userId);
        User user = userRepository.findById(userId).get();

        Booking booking = BookingMapper.returnBooking(bookingDto);
        booking.setItem(item);
        booking.setBooker(user);

        if (item.getOwner().equals(user)) {
            throw new NotFoundException(User.class, "Owner " + userId + " can't book his item");
        }
        if (!item.getAvailable()) {
            throw new ValidationException("Item " + item.getId() + " is booked");
        }
        if (booking.getStart().isAfter(booking.getEnd())) {
            throw new ValidationException("Start cannot be later than end");
        }
        if (booking.getStart().isEqual(booking.getEnd())) {
            throw new ValidationException("Start cannot be equal than end");
        }

        bookingRepository.save(booking);

        return BookingMapper.returnBookingDto(booking);
    }

    @Transactional
    @Override
    public BookingOutDto approveBooking(long userId, long bookingId, Boolean approved) {

        checkService.checkBooking(bookingId);
        Booking booking = bookingRepository.findById(bookingId).get();

        if (booking.getItem().getOwner().getId() != userId) {
            throw new NotFoundException(User.class, "Only owner " + userId + " items can change booking status");
        }

        if (approved) {
            if (booking.getStatus().equals(BookingStatus.APPROVED)) {
                throw new ValidationException("Incorrect status update request");
            }
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        bookingRepository.save(booking);
        return BookingMapper.returnBookingDto(booking);
    }

    @Transactional(readOnly = true)
    @Override
    public BookingOutDto getBookingById(long userId, long bookingId) {

        checkService.checkBooking(bookingId);
        Booking booking = bookingRepository.findById(bookingId).get();

        checkService.checkUser(userId);

        if (booking.getBooker().getId() == userId || booking.getItem().getOwner().getId() == userId) {
            return BookingMapper.returnBookingDto(booking);
        } else {
            throw new NotFoundException(User.class, "To get information about the reservation, the car of the reservation or the owner {} " + userId + "of the item can");
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingOutDto> getAllBookingsByBookerId(long userId, String state, Integer from, Integer size) {

        checkService.checkUser(userId);
        PageRequest pageRequest = checkService.checkPageSize(from, size);

        Page<Booking> bookings = null;

        BookingState bookingState = BookingState.getEnumValue(state);

        switch (bookingState) {
            case ALL:
                bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(userId, pageRequest);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartAsc(userId, LocalDateTime.now(), LocalDateTime.now(), pageRequest);
                break;
            case PAST:
                bookings = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now(), pageRequest);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now(), pageRequest);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING, pageRequest);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED, pageRequest);
                break;

        }
        return BookingMapper.returnBookingDtoList(bookings);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingOutDto> getAllBookingsForAllItemsByOwnerId(long userId, String state, Integer from, Integer size) {

        checkService.checkUser(userId);
        PageRequest pageRequest = checkService.checkPageSize(from, size);

        if (itemRepository.findByOwnerId(userId).isEmpty()) {
            throw new ValidationException("User does not have items to booking");
        }
        Page<Booking> bookings = null;

        BookingState bookingState = BookingState.getEnumValue(state);

        switch (bookingState) {
            case ALL:
                bookings = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId, pageRequest);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartAsc(userId, LocalDateTime.now(), LocalDateTime.now(), pageRequest);
                break;
            case PAST:
                bookings = bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now(), pageRequest);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now(), pageRequest);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING, pageRequest);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED, pageRequest);
                break;
        }
        return BookingMapper.returnBookingDtoList(bookings);
    }
}
