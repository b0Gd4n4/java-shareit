package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;


    @Override
    public Item addItem(long userId, Item item) {

        userRepository.get(userId);
        item.setOwner(userId);
        return itemRepository.add(item);
    }

    public Item updateItem(Item item, long itemId, long userId) {
        userRepository.get(userId);
        Item storedItem = itemRepository.get(itemId);
        if (storedItem.getOwner() != userId) {
            throw new NotFoundException(Item.class, "the item was not found with the user id " + userId);
        }
        item.setId(itemId);
        item.setOwner(userId);
        if (item.getName() != null) {
            storedItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            storedItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            storedItem.setAvailable(item.getAvailable());
        }
        itemRepository.update(storedItem);
        return storedItem;
    }

    @Override
    public Item getItemById(long userId) {
        return itemRepository.get(userId);
    }


    @Override
    public List<Item> getItemsUser(long userId) {

        userRepository.get(userId);
        return itemRepository.getItemListByUserId(userId);
    }

    @Override
    public List<Item> searchItem(String text) {

        return itemRepository.search(text);
    }




}