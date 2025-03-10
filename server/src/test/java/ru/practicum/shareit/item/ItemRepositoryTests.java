package ru.practicum.shareit.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class ItemRepositoryTests {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemRepository itemRepository;

    Item firstitem;
    Item secondItem;
    User user;

    @BeforeEach
    void setUp() {

        user = User.builder()
                .name("Alex")
                .email("alexFirst@yandex.ru")
                .build();
        userRepository.save(user);

        firstitem = Item.builder()
                .name("itemName")
                .description("item description")
                .available(true)
                .owner(user)
                .build();
        itemRepository.save(firstitem);

        secondItem = Item.builder()
                .name("item")
                .description("description for search test")
                .available(true)
                .owner(user)
                .build();
        itemRepository.save(secondItem);
    }

    @Test
    void searchTest() {

        List<Item> descriptionSearchResult = itemRepository.search("search");

        assertEquals(1, descriptionSearchResult.size());
        assertEquals("item", descriptionSearchResult.get(0).getName());

        List<Item> nameSearchResult = itemRepository.search("item");
        assertEquals(2, nameSearchResult.size());
        assertEquals("item", descriptionSearchResult.get(0).getName());
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
    }

}
