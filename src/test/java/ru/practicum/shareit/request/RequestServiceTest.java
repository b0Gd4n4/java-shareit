package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.check.CheckService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
public class RequestServiceTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @MockBean
    private ItemRequestRepository itemRequestRepository;

    @MockBean
    private ItemRepository itemRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private CheckService checkService;


    private User firstUser;
    private User secondUser;
    private ItemRequest firstItemRequest;
    private ItemRequest secondItemRequest;
    private ItemRequestDto itemRequestDto;
    private Item item;

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
                .email("nameOne@yandex.ru")
                .build();

        firstItemRequest = ItemRequest.builder()
                .id(1L)
                .description("ItemRequest One")
                .created(LocalDateTime.now())
                .build();

        secondItemRequest = ItemRequest.builder()
                .id(2L)
                .description("ItemRequest Two")
                .created(LocalDateTime.now())
                .build();

        item = Item.builder()
                .id(1L)
                .name("fdhehjgdeh")
                .description("new name")
                .available(true)
                .owner(firstUser)
                .request(firstItemRequest)
                .build();

        itemRequestDto = ItemRequestDto.builder().description("ItemRequest One").build();
    }

    @Test
    void createRequest() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(firstUser));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(firstItemRequest);

        ItemRequestDto itemRequestDtoTest = itemRequestService.createRequest(itemRequestDto, firstUser.getId());

        assertEquals(itemRequestDtoTest.getId(), firstItemRequest.getId());
        assertEquals(itemRequestDtoTest.getDescription(), firstItemRequest.getDescription());

        verify(itemRequestRepository, times(1)).save(any(ItemRequest.class));
    }

    @Test
    void getRequests() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRequestRepository.findByRequesterIdOrderByCreatedAsc(anyLong())).thenReturn(List.of(firstItemRequest));
        when(itemRepository.findByRequestId(anyLong())).thenReturn(List.of(item));

        ItemRequestDto itemRequestDtoTest = itemRequestService.getRequests(firstUser.getId()).get(0);

        assertEquals(itemRequestDtoTest.getItems().get(0).getId(), item.getId());
        assertEquals(itemRequestDtoTest.getItems().get(0).getName(), item.getName());
        assertEquals(itemRequestDtoTest.getItems().get(0).getDescription(), item.getDescription());
        assertEquals(itemRequestDtoTest.getItems().get(0).getAvailable(), item.getAvailable());

        verify(itemRequestRepository, times(1)).findByRequesterIdOrderByCreatedAsc(anyLong());
    }

    @Test
    void getAllRequests() {
        when(checkService.checkPageSize(anyInt(), anyInt())).thenReturn(PageRequest.of(5 / 10,10));
        when(itemRequestRepository.findByIdIsNotOrderByCreatedAsc(anyLong(), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(firstItemRequest)));
        when(itemRepository.findByRequestId(anyLong())).thenReturn(List.of(item));

        ItemRequestDto itemRequestDtoTest = itemRequestService.getAllRequests(firstUser.getId(), 5, 10).get(0);

        assertEquals(itemRequestDtoTest.getItems().get(0).getId(), item.getId());
        assertEquals(itemRequestDtoTest.getItems().get(0).getName(), item.getName());
        assertEquals(itemRequestDtoTest.getItems().get(0).getDescription(), item.getDescription());
        assertEquals(itemRequestDtoTest.getItems().get(0).getAvailable(), item.getAvailable());

        verify(itemRequestRepository, times(1)).findByIdIsNotOrderByCreatedAsc(anyLong(),any(PageRequest.class));
    }

    @Test
    void getRequestById() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRequestRepository.existsById(anyLong())).thenReturn(true);
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.ofNullable(firstItemRequest));
        when(itemRepository.findByRequestId(anyLong())).thenReturn(List.of(item));


        ItemRequestDto itemRequestDtoTest = itemRequestService.getRequestById(firstUser.getId(), firstItemRequest.getId());

        assertEquals(itemRequestDtoTest.getId(), firstItemRequest.getId());
        assertEquals(itemRequestDtoTest.getDescription(), firstItemRequest.getDescription());
        assertEquals(itemRequestDtoTest.getItems().get(0).getId(), item.getId());
        assertEquals(itemRequestDtoTest.getItems().get(0).getRequestId(), firstUser.getId());

        verify(itemRequestRepository, times(1)).findById(anyLong());
    }

    @Test
    void createItemsToRequest() {
        when(itemRepository.findByRequestId(anyLong())).thenReturn(List.of(item));

        ItemRequestDto itemRequestDtoTest = itemRequestService.createItemsToRequest(firstItemRequest);

        assertEquals(itemRequestDtoTest.getItems().get(0).getId(), item.getId());
        assertEquals(itemRequestDtoTest.getItems().get(0).getRequestId(), firstUser.getId());

        verify(itemRepository, times(1)).findByRequestId(anyLong());
    }
}