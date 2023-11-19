package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.check.CheckService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;


import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final CheckService checkService;

    @Transactional
    @Override
    public ItemDto createItem(long userId, ItemDto itemDto) {

        checkService.checkUser(userId);

        User user = userRepository.findById(userId).get();
        Item item = ItemMapper.returnItem(itemDto, user);

        if (itemDto.getRequestId() != null) {
            checkService.checkRequest(itemDto.getRequestId());
            item.setRequest(itemRequestRepository.findById(itemDto.getRequestId()).get());
        }
        itemRepository.save(item);

        return ItemMapper.returnItemDto(item);
    }

    @Transactional
    @Override
    public ItemDto updateItem(ItemDto itemDto, long itemId, long userId) {

        checkService.checkUser(userId);
        User user = userRepository.findById(userId).get();

        checkService.checkItem(itemId);
        Item item = ItemMapper.returnItem(itemDto, user);

        item.setId(itemId);

        if (!itemRepository.findByOwnerId(userId).contains(item)) {
            throw new NotFoundException(Item.class, "the item was not found with the user id " + userId);
        }

        Item newItem = itemRepository.findById(item.getId()).get();

        if (item.getName() != null) {
            newItem.setName(item.getName());
        }

        if (item.getDescription() != null) {
            newItem.setDescription(item.getDescription());
        }

        if (item.getAvailable() != null) {
            newItem.setAvailable(item.getAvailable());
        }

        itemRepository.save(newItem);

        return ItemMapper.returnItemDto(newItem);
    }

    @Transactional(readOnly = true)
    @Override
    public ItemDto getItemById(long itemId, long userId) {

        checkService.checkItem(itemId);
        Item item = itemRepository.findById(itemId).get();

        ItemDto itemDto = ItemMapper.returnItemDto(item);

        checkService.checkUser(userId);

        if (item.getOwner().getId() == userId) {

            Optional<Booking> lastBooking = bookingRepository.findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(itemId, BookingStatus.APPROVED, LocalDateTime.now());
            Optional<Booking> nextBooking = bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(itemId, BookingStatus.APPROVED, LocalDateTime.now());

            if (lastBooking.isPresent()) {
                itemDto.setLastBooking(BookingMapper.returnBookingShortDto(lastBooking.get()));
            } else {
                itemDto.setLastBooking(null);
            }

            if (nextBooking.isPresent()) {
                itemDto.setNextBooking(BookingMapper.returnBookingShortDto(nextBooking.get()));
            } else {
                itemDto.setNextBooking(null);
            }
        }

        List<Comment> commentList = commentRepository.findAllByItemId(itemId);

        if (!commentList.isEmpty()) {
            itemDto.setComments(CommentMapper.returnICommentDtoList(commentList));
        } else {
            itemDto.setComments(Collections.emptyList());
        }

        return itemDto;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemDto> getItemsUser(long userId, Integer from, Integer size) {

        checkService.checkUser(userId);
        PageRequest pageRequest = PageRequest.of(from / size, size);

        List<ItemDto> resultList = new ArrayList<>();

        for (ItemDto itemDto : ItemMapper.returnItemDtoList(itemRepository.findByOwnerIdOrderById(userId, pageRequest))) {

            Optional<Booking> lastBooking = bookingRepository.findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(itemDto.getId(), BookingStatus.APPROVED, LocalDateTime.now());
            Optional<Booking> nextBooking = bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(itemDto.getId(), BookingStatus.APPROVED, LocalDateTime.now());

            if (lastBooking.isPresent()) {
                itemDto.setLastBooking(BookingMapper.returnBookingShortDto(lastBooking.get()));
            } else {
                itemDto.setLastBooking(null);
            }

            if (nextBooking.isPresent()) {
                itemDto.setNextBooking(BookingMapper.returnBookingShortDto(nextBooking.get()));
            } else {
                itemDto.setNextBooking(null);
            }

            resultList.add(itemDto);
        }

        for (ItemDto itemDto : resultList) {

            List<Comment> commentList = commentRepository.findAllByItemId(itemDto.getId());

            if (!commentList.isEmpty()) {
                itemDto.setComments(CommentMapper.returnICommentDtoList(commentList));
            } else {
                itemDto.setComments(Collections.emptyList());
            }
        }

        return resultList;
    }

    @Transactional(readOnly = true)
    @Override
    public  List<ItemDto> searchItem(String text, Integer from, Integer size) {

        PageRequest pageRequest = PageRequest.of(from / size, size);

        if (text.equals("")) {
            return Collections.emptyList();
        } else {
            return ItemMapper.returnItemDtoList(itemRepository.search(text, pageRequest));
        }
    }

    @Transactional
    @Override
    public CommentDto createComment(long userId, long itemId, CommentDto commentDto) {

        checkService.checkUser(userId);
        User user = userRepository.findById(userId).get();

        checkService.checkItem(itemId);
        Item item = itemRepository.findById(itemId).get();

        LocalDateTime dateTime = LocalDateTime.now();

        Optional<Booking> booking = bookingRepository.findFirstByItemIdAndBookerIdAndStatusAndEndBefore(itemId, userId, BookingStatus.APPROVED, dateTime);

        if (booking.isEmpty()) {
            throw new ValidationException("User " + userId + " not booking this item " + itemId);
        }

        Comment comment = CommentMapper.returnComment(commentDto, item, user, dateTime);

        return CommentMapper.returnCommentDto(commentRepository.save(comment));
    }
}