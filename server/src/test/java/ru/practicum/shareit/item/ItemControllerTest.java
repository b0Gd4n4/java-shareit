package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {

    @MockBean
    private ItemService itemService;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    private ItemDto firstItemDto;

    private ItemDto secondItemDto;

    private CommentDto commentDto;

    private ItemRequest itemRequest;

    private User user;

    @BeforeEach
    void beforeEach() {

        user = User.builder()
                .id(1L)
                .name("Name")
                .email("name@yandex.ru")
                .build();

        itemRequest = ItemRequest.builder()
                .id(1L)
                .description("Name")
                .requester(user)
                .created(LocalDateTime.now())
                .build();

        commentDto = CommentDto.builder()
                .id(1L)
                .text("fdhehjgdeh")
                .created(LocalDateTime.now())
                .authorName("NameOne")
                .build();

        firstItemDto = ItemDto.builder()
                .id(1L)
                .name("NameTwo")
                .description("new name")
                .available(true)
                .comments(List.of(commentDto))
                .requestId(itemRequest.getId())
                .build();

        secondItemDto = ItemDto.builder()
                .id(1L)
                .name("guitar")
                .description("a very good tool")
                .available(true)
                .comments(Collections.emptyList())
                .requestId(itemRequest.getId())
                .build();
    }

    @Test
    void createItem() throws Exception {
        when(itemService.createItem(anyLong(), any(ItemDto.class))).thenReturn(firstItemDto);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(firstItemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(firstItemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(firstItemDto.getName()), String.class))
                .andExpect(jsonPath("$.description", is(firstItemDto.getDescription()), String.class))
                .andExpect(jsonPath("$.available", is(firstItemDto.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$.requestId", is(firstItemDto.getRequestId()), Long.class));

        verify(itemService, times(1)).createItem(1L, firstItemDto);
    }

    @Test
    void updateItem() throws Exception {
        when(itemService.updateItem(any(ItemDto.class), anyLong(), anyLong())).thenReturn(firstItemDto);

        mvc.perform(patch("/items/{itemId}", 1L)
                        .content(mapper.writeValueAsString(firstItemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(firstItemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(firstItemDto.getName()), String.class))
                .andExpect(jsonPath("$.description", is(firstItemDto.getDescription()), String.class))
                .andExpect(jsonPath("$.available", is(firstItemDto.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$.requestId", is(firstItemDto.getRequestId()), Long.class));

        verify(itemService, times(1)).updateItem(firstItemDto, 1L, 1L);
    }

    @Test
    void getItemById() throws Exception {
        when(itemService.getItemById(anyLong(), anyLong())).thenReturn(firstItemDto);

        mvc.perform(get("/items/{itemId}", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(firstItemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(firstItemDto.getName()), String.class))
                .andExpect(jsonPath("$.description", is(firstItemDto.getDescription()), String.class))
                .andExpect(jsonPath("$.available", is(firstItemDto.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$.requestId", is(firstItemDto.getRequestId()), Long.class));

        verify(itemService, times(1)).getItemById(1L, 1L);
    }

    @Test
    void getAllItemsUser() throws Exception {

        when(itemService.getItemsUser(anyLong(), anyInt(), anyInt())).thenReturn(List.of(firstItemDto, secondItemDto));

        mvc.perform(get("/items")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(firstItemDto, secondItemDto))));

        verify(itemService, times(1)).getItemsUser(1L, 0, 10);
    }

    @Test
    void getSearchItem() throws Exception {
        when(itemService.searchItem(anyString(), anyInt(), anyInt())).thenReturn(List.of(firstItemDto, secondItemDto));

        mvc.perform(get("/items/search")
                        .param("text", "text")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(firstItemDto, secondItemDto))));

        verify(itemService, times(1)).searchItem("text", 0, 10);
    }

    @Test
    void createComment() throws Exception {
        when(itemService.createComment(anyLong(), anyLong(), any(CommentDto.class))).thenReturn(commentDto);

        mvc.perform(post("/items/{itemId}/comment", 1L)
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(commentDto)));

        verify(itemService, times(1)).createComment(1L, 1L, commentDto);
    }
}