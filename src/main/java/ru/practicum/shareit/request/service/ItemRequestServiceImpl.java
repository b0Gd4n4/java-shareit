package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.practicum.shareit.check.CheckService;
import ru.practicum.shareit.item.ItemMapper;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;


import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final CheckService checkService;

    @Transactional
    @Override
    public ItemRequestDto createRequest(ItemRequestDto itemRequestDto, long userId) {

        checkService.checkUser(userId);

        User user = userRepository.findById(userId).get();

        ItemRequest itemRequest = ItemRequestMapper.returnItemRequest(itemRequestDto, user);

        itemRequest = itemRequestRepository.save(itemRequest);

        return ItemRequestMapper.returnItemRequestDto(itemRequest);
    }

    @Override
    public List<ItemRequestDto> getRequests(long userId) {

        checkService.checkUser(userId);

        List<ItemRequest> itemRequests = itemRequestRepository.findByRequesterIdOrderByCreatedAsc(userId);

        List<ItemRequestDto> result = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequests) {
            result.add(createItemsToRequest(itemRequest));
        }
        return result;
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Long userId, Integer from, Integer size) {

        PageRequest pageRequest = checkService.checkPageSize(from, size);

        Page<ItemRequest> itemRequests = itemRequestRepository.findByIdIsNotOrderByCreatedAsc(userId, pageRequest);

        List<ItemRequestDto> result = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequests) {
            result.add(createItemsToRequest(itemRequest));
        }
        return result;
    }

    @Override
    public ItemRequestDto getRequestById(long userId, long requestId) {

        checkService.checkUser(userId);
        checkService.checkRequest(requestId);

        ItemRequest itemRequest = itemRequestRepository.findById(requestId).get();

        return createItemsToRequest(itemRequest);
    }

    @Override
    public ItemRequestDto createItemsToRequest(ItemRequest itemRequest) {

        ItemRequestDto itemRequestDto = ItemRequestMapper.returnItemRequestDto(itemRequest);
        List<Item> items = itemRepository.findByRequestId(itemRequest.getId());
        itemRequestDto.setItems(ItemMapper.returnItemDtoList(items));

        return itemRequestDto;
    }
}