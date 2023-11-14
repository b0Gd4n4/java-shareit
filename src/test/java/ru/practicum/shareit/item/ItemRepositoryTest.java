package ru.practicum.shareit.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemRepository itemRepository;

    Item firstItem;

    Item secondItem;

    User user;

    @BeforeEach
    void beforeEach() {

        user = userRepository.save(User.builder()
                .id(1L)
                .name("Name")
                .email("name@yandex.ru")
                .build());

        firstItem = itemRepository.save(Item.builder()
                .name("wewdfrgrsg")
                .description("new name, new work")
                .available(true)
                .owner(user)
                .build());

        secondItem = itemRepository.save(Item.builder()
                .name("NameOne")
                .description("a very good name")
                .available(true)
                .owner(user)
                .build());
    }

    @Test
    void search() {

        List<Item> items = itemRepository.search("very", PageRequest.of(0, 1));

        assertEquals(1, items.size());
        assertEquals("NameOne", items.get(0).getName());
    }

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
    }
}