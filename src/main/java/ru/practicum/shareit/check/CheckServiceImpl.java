package ru.practicum.shareit.check;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;


@Service
@RequiredArgsConstructor
public class CheckServiceImpl implements CheckService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public void checkUser(Long userId) {

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(User.class, "User id " + userId + " not found.");
        }
    }

    @Override
    public void checkItem(Long itemId) {

        if (!itemRepository.existsById(itemId)) {
            throw new NotFoundException(Item.class, "Item id " + itemId + " not found.");
        }
    }

    @Override
    public void checkBooking(Long bookingId) {

        if (!bookingRepository.existsById(bookingId)) {
            throw new NotFoundException(Booking.class, "Booking id " + bookingId + " not found.");
        }
    }

    @Override
    public void checkRequest(Long requestId) {

        if (!itemRequestRepository.existsById(requestId)) {
            throw new NotFoundException(ItemRequest.class, "Request id " + requestId + " not found.");
        }
    }

    @Override
    public PageRequest checkPageSize(Integer from, Integer size) {

        if (from == 0 && size == 0) {
            throw new ValidationException("\"size\" and \"from\"must be not equal 0");
        }

        if (size <= 0) {
            throw new ValidationException("\"size\" must be greater than 0");
        }

        if (from < 0) {
            throw new ValidationException("\"from\" must be greater than or equal to 0");
        }
        return PageRequest.of(from / size, size);
    }
}
