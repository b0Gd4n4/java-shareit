package ru.practicum.shareit.service;


import ru.practicum.shareit.dto.ItemRequestDto;
import ru.practicum.shareit.model.ItemRequest;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto createRequest(ItemRequestDto itemRequestDto, long userId);

    List<ItemRequestDto> getRequests(long userId);

    List<ItemRequestDto> getAllRequests(Long userId, Integer from, Integer size);

    ItemRequestDto getRequestById(long userId, long requestId);

    ItemRequestDto createItemsToRequest(ItemRequest itemRequest);
}