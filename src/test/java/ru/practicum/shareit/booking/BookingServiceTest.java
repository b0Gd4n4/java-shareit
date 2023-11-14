package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.check.CheckService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
public class BookingServiceTest {

    @Autowired
    private BookingService bookingService;

    @MockBean
    private CheckService checkService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private ItemRepository itemRepository;

    @MockBean
    private BookingRepository bookingRepository;

    private User firstUser;

    private User secondUser;

    private Item item;

    private ItemDto itemDto;

    private Booking firstBooking;

    private Booking secondBooking;

    private BookingDto bookingDto;

    @BeforeEach
    void beforeEach() {
        firstUser = User.builder()
                .id(1L)
                .name("Name")
                .email("name@yandex.ru")
                .build();

        secondUser = User.builder()
                .id(2L)
                .name("NameOne")
                .email("nameone@yandex.ru")
                .build();

        item = Item.builder()
                .id(1L)
                .name("fjvdrhdlvhe")
                .description("new name, new work")
                .available(true)
                .owner(firstUser)
                .build();

        itemDto = ItemMapper.returnItemDto(item);

        firstBooking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now())
                .item(item)
                .booker(firstUser)
                .status(BookingStatus.APPROVED)
                .build();

        secondBooking = Booking.builder()
                .id(2L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now())
                .item(item)
                .booker(firstUser)
                .status(BookingStatus.WAITING)
                .build();

        bookingDto = BookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.of(2023, 7, 5, 0, 0))
                .end(LocalDateTime.of(2023, 10, 12, 0, 0))
                .status(BookingStatus.APPROVED)
                .build();
    }

    @Test
    void createBooking() {
        when(itemRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(secondUser));
        when(bookingRepository.save(any(Booking.class))).thenReturn(firstBooking);

        BookingOutDto bookingOutDtoTest = bookingService.createBooking(bookingDto, anyLong());

        assertEquals(bookingOutDtoTest.getItem(), itemDto);
        assertEquals(bookingOutDtoTest.getStatus(), firstBooking.getStatus());
        assertEquals(bookingOutDtoTest.getBooker(), UserMapper.returnUserDto(secondUser));

        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void createBookingWrongOwner() {
        when(itemRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(firstUser));

        assertThrows(NotFoundException.class, () -> bookingService.createBooking(bookingDto, anyLong()));
    }

    @Test
    void  createBookingItemBooked() {

        item.setAvailable(false);

        when(itemRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(secondUser));

        assertThrows(ValidationException.class, () -> bookingService.createBooking(bookingDto, anyLong()));
    }

    @Test
    void  createBookingNotValidEnd() {
        when(itemRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(secondUser));

        bookingDto.setEnd(LocalDateTime.of(2022, 10, 12, 0, 0));

        assertThrows(ValidationException.class, () -> bookingService.createBooking(bookingDto, anyLong()));
    }

    @Test
    void createBookingNotValidStart() {
        when(itemRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(secondUser));

        bookingDto.setStart(LocalDateTime.of(2023, 10, 12, 0, 0));

        assertThrows(ValidationException.class, () -> bookingService.createBooking(bookingDto, anyLong()));
    }

    @Test
    void  approveBooking() {
        BookingOutDto bookingOutDtoTest;

        when(bookingRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(secondBooking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(secondBooking);

        bookingOutDtoTest = bookingService.approveBooking(firstUser.getId(), item.getId(), true);
        assertEquals(bookingOutDtoTest.getStatus(), BookingStatus.APPROVED);

        bookingOutDtoTest = bookingService.approveBooking(firstUser.getId(), item.getId(), false);
        assertEquals(bookingOutDtoTest.getStatus(), BookingStatus.REJECTED);

        verify(bookingRepository, times(2)).save(any(Booking.class));
    }

    @Test
    void  approveBookingWrongUser() {
        when(bookingRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(secondBooking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(secondBooking);

        assertThrows(NotFoundException.class, () -> bookingService.approveBooking(secondUser.getId(), item.getId(), true));
    }

    @Test
    void  approveBookingWrongStatus() {
        when(bookingRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(firstBooking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(firstBooking);

        assertThrows(ValidationException.class, () -> bookingService.approveBooking(firstUser.getId(), item.getId(), true));
    }

    @Test
    void  getBookingById() {
        when(bookingRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(firstBooking));
        when(userRepository.existsById(anyLong())).thenReturn(true);

        BookingOutDto bookingOutDtoTest = bookingService.getBookingById(firstUser.getId(), firstBooking.getId());

        assertEquals(bookingOutDtoTest.getItem(), itemDto);
        assertEquals(bookingOutDtoTest.getStatus(), firstBooking.getStatus());
        assertEquals(bookingOutDtoTest.getBooker(), UserMapper.returnUserDto(firstUser));

    }

    @Test
    void  getBookingByIdError() {
        when(bookingRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(firstBooking));
        when(userRepository.existsById(anyLong())).thenReturn(true);

        assertThrows(NotFoundException.class, () -> bookingService.getBookingById(2L, firstBooking.getId()));
    }

    @Test
    void getAllBookingsByBookerId() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(checkService.checkPageSize(anyInt(), anyInt())).thenReturn(PageRequest.of(5 / 10,10));
        when(bookingRepository.findAllByBookerIdOrderByStartDesc(anyLong(), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(firstBooking)));

        String state = "ALL";

        List<BookingOutDto> bookingOutDtoTest = bookingService. getAllBookingsByBookerId(firstUser.getId(), state, 5, 10);

        assertEquals(bookingOutDtoTest.get(0).getId(), firstBooking.getId());
        assertEquals(bookingOutDtoTest.get(0).getStatus(), firstBooking.getStatus());
        assertEquals(bookingOutDtoTest.get(0).getBooker(), UserMapper.returnUserDto(firstUser));

        when(bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartAsc(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(firstBooking)));
        state = "CURRENT";

        bookingOutDtoTest = bookingService. getAllBookingsByBookerId(firstUser.getId(), state, 5, 10);

        assertEquals(bookingOutDtoTest.get(0).getId(), firstBooking.getId());
        assertEquals(bookingOutDtoTest.get(0).getStatus(), firstBooking.getStatus());
        assertEquals(bookingOutDtoTest.get(0).getBooker(), UserMapper.returnUserDto(firstUser));

        when(bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(anyLong(), any(LocalDateTime.class), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(firstBooking)));
        state = "PAST";

        bookingOutDtoTest = bookingService. getAllBookingsByBookerId(firstUser.getId(), state, 5, 10);

        assertEquals(bookingOutDtoTest.get(0).getId(), firstBooking.getId());
        assertEquals(bookingOutDtoTest.get(0).getStatus(), firstBooking.getStatus());
        assertEquals(bookingOutDtoTest.get(0).getBooker(), UserMapper.returnUserDto(firstUser));

        when(bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(anyLong(), any(LocalDateTime.class), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(firstBooking)));
        state = "FUTURE";

        bookingOutDtoTest = bookingService. getAllBookingsByBookerId(firstUser.getId(), state, 5, 10);

        assertEquals(bookingOutDtoTest.get(0).getId(), firstBooking.getId());
        assertEquals(bookingOutDtoTest.get(0).getStatus(), firstBooking.getStatus());
        assertEquals(bookingOutDtoTest.get(0).getBooker(), UserMapper.returnUserDto(firstUser));

        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(anyLong(), any(BookingStatus.class), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(firstBooking)));
        state = "WAITING";

        bookingOutDtoTest = bookingService. getAllBookingsByBookerId(firstUser.getId(), state, 5, 10);

        assertEquals(bookingOutDtoTest.get(0).getId(), firstBooking.getId());
        assertEquals(bookingOutDtoTest.get(0).getStatus(), firstBooking.getStatus());
        assertEquals(bookingOutDtoTest.get(0).getBooker(), UserMapper.returnUserDto(firstUser));

        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(anyLong(), any(BookingStatus.class), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(firstBooking)));
        state = "REJECTED";

        bookingOutDtoTest = bookingService. getAllBookingsByBookerId(firstUser.getId(), state, 5, 10);

        assertEquals(bookingOutDtoTest.get(0).getId(), firstBooking.getId());
        assertEquals(bookingOutDtoTest.get(0).getStatus(), firstBooking.getStatus());
        assertEquals(bookingOutDtoTest.get(0).getBooker(), UserMapper.returnUserDto(firstUser));
    }

    @Test
    void getAllBookingsForAllItemsByOwnerId() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findByOwnerId(anyLong())).thenReturn(List.of(item));
        when(checkService.checkPageSize(anyInt(), anyInt())).thenReturn(PageRequest.of(5 / 10,10));
        when(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(anyLong(), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(firstBooking)));

        String state = "ALL";

        List<BookingOutDto> bookingOutDtoTest = bookingService.getAllBookingsForAllItemsByOwnerId(firstUser.getId(), state, 5, 10);

        assertEquals(bookingOutDtoTest.get(0).getId(), firstBooking.getId());
        assertEquals(bookingOutDtoTest.get(0).getStatus(), firstBooking.getStatus());
        assertEquals(bookingOutDtoTest.get(0).getBooker(), UserMapper.returnUserDto(firstUser));

        when(bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartAsc(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(firstBooking)));
        state = "CURRENT";

        bookingOutDtoTest = bookingService.getAllBookingsForAllItemsByOwnerId(firstUser.getId(), state, 5, 10);

        assertEquals(bookingOutDtoTest.get(0).getId(), firstBooking.getId());
        assertEquals(bookingOutDtoTest.get(0).getStatus(), firstBooking.getStatus());
        assertEquals(bookingOutDtoTest.get(0).getBooker(), UserMapper.returnUserDto(firstUser));

        when(bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(anyLong(), any(LocalDateTime.class), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(firstBooking)));
        state = "PAST";

        bookingOutDtoTest = bookingService.getAllBookingsForAllItemsByOwnerId(firstUser.getId(), state, 5, 10);

        assertEquals(bookingOutDtoTest.get(0).getId(), firstBooking.getId());
        assertEquals(bookingOutDtoTest.get(0).getStatus(), firstBooking.getStatus());
        assertEquals(bookingOutDtoTest.get(0).getBooker(), UserMapper.returnUserDto(firstUser));

        when(bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(anyLong(), any(LocalDateTime.class), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(firstBooking)));
        state = "FUTURE";

        bookingOutDtoTest = bookingService.getAllBookingsForAllItemsByOwnerId(firstUser.getId(), state, 5, 10);

        assertEquals(bookingOutDtoTest.get(0).getId(), firstBooking.getId());
        assertEquals(bookingOutDtoTest.get(0).getStatus(), firstBooking.getStatus());
        assertEquals(bookingOutDtoTest.get(0).getBooker(), UserMapper.returnUserDto(firstUser));

        when(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(anyLong(), any(BookingStatus.class), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(firstBooking)));
        state = "WAITING";

        bookingOutDtoTest = bookingService.getAllBookingsForAllItemsByOwnerId(firstUser.getId(), state, 5, 10);

        assertEquals(bookingOutDtoTest.get(0).getId(), firstBooking.getId());
        assertEquals(bookingOutDtoTest.get(0).getStatus(), firstBooking.getStatus());
        assertEquals(bookingOutDtoTest.get(0).getBooker(), UserMapper.returnUserDto(firstUser));

        when(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(anyLong(), any(BookingStatus.class), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(firstBooking)));
        state = "REJECTED";

        bookingOutDtoTest = bookingService.getAllBookingsForAllItemsByOwnerId(firstUser.getId(), state, 5, 10);

        assertEquals(bookingOutDtoTest.get(0).getId(), firstBooking.getId());
        assertEquals(bookingOutDtoTest.get(0).getStatus(), firstBooking.getStatus());
        assertEquals(bookingOutDtoTest.get(0).getBooker(), UserMapper.returnUserDto(firstUser));
    }

    @Test
    void getAllBookingsForAllItemsByOwnerIdNotHaveItems() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findByOwnerId(anyLong())).thenReturn(List.of());

        assertThrows(ValidationException.class, () -> bookingService.getAllBookingsForAllItemsByOwnerId(firstUser.getId(), "APPROVED", 5, 10));
    }
}