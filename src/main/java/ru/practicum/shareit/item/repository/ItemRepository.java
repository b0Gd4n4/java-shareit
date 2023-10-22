package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {

    Item get(long itemId);

    List<Item> getAll();

    Item add(Item item);

    void update(Item item);

    List<Item> getItemListByUserId(long userId);

    List<Item> search(String text);


}