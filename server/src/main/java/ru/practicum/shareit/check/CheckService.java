package ru.practicum.shareit.check;

public interface CheckService {
    void checkUser(Long userId);

    void checkItem(Long itemId);

    void checkBooking(Long booking);

    void checkRequest(Long requestId);

}
