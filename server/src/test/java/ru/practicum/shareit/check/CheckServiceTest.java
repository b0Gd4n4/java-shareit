package ru.practicum.shareit.check;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;


import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest
public class CheckServiceTest {

    @Autowired
    private CheckService checkService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private ItemRepository itemRepository;

    @MockBean
    private BookingRepository bookingRepository;

    @MockBean
    private ItemRequestRepository itemRequestRepository;

    @Test
    void checkUser() {
        when(userRepository.existsById(anyLong())).thenReturn(false);
        assertThrows(NotFoundException.class, () -> checkService.checkUser(1L));
    }

    @Test
    void checkItem() {
        when(itemRepository.existsById(anyLong())).thenReturn(false);
        assertThrows(NotFoundException.class, () -> checkService.checkItem(1L));
    }

    @Test
    void checkBooking() {
        when(bookingRepository.existsById(anyLong())).thenReturn(false);
        assertThrows(NotFoundException.class, () -> checkService.checkBooking(1L));
    }

    @Test
    void checkRequest() {
        when(itemRequestRepository.existsById(anyLong())).thenReturn(false);
        assertThrows(NotFoundException.class, () -> checkService.checkRequest(1L));
    }
}