package ru.practicum.shareit.item;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.ShareItApp;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = ShareItApp.class)
class ItemServiceTests {

    @Autowired
    private EntityManager em;
    private UserDto user1;
    private UserDto user2;
    private UserDto user3;
    private ItemDto item1;
    private ItemDto item2;
    private ItemDto item3;
    private TypedQuery<Item> queryFindById;

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private BookingService bookingService;
    @Autowired
    private ItemServiceImpl itemService;
    private UserMapper userMapper;

    ItemServiceTests() {
    }

    @BeforeEach
    void setUp() {
        userMapper = new UserMapper();
        String uniqueEmail1 = "ivan" + System.currentTimeMillis() + "@yandex.ru";
        String uniqueEmail2 = "petr" + (System.currentTimeMillis() + 1) + "@yandex.ru";
        String uniqueEmail3 = "sidor" + (System.currentTimeMillis() + 2) + "@yandex.ru";

        UserDto userDto1 = UserDto.builder()
                .name("Ivan")
                .email(uniqueEmail1)
                .build();

        UserDto userDto2 = UserDto.builder()
                .name("Petr")
                .email(uniqueEmail2)
                .build();

        UserDto userDto3 = UserDto.builder()
                .name("Sidor")
                .email(uniqueEmail3)
                .build();

        user1 = userService.addUser(userDto1);
        user2 = userService.addUser(userDto2);
        user3 = userService.addUser(userDto3);

        ItemDto itemDto1 = ItemDto.builder()
                .name("Item1")
                .description("Description1")
                .available(true)
                .build();

        ItemDto itemDto2 = ItemDto.builder()
                .name("Item2")
                .description("Description2")
                .available(true)
                .build();

        ItemDto itemDto3 = ItemDto.builder()
                .name("Item3")
                .description("Description3")
                .available(true)
                .build();

        item1 = itemService.addItem(user1.getId(), itemDto1);
        item2 = itemService.addItem(user1.getId(), itemDto2);
        item3 = itemService.addItem(user2.getId(), itemDto3);

        queryFindById = em.createQuery("Select i from Item i where i.id = :id", Item.class);
    }

    @Test
    void addItemTesting() {
        ItemDto toCreate = ItemDto.builder()
                .name("Item1")
                .description("Description1")
                .available(true)
                .build();

        ItemDto itemDto = itemService.addItem(user1.getId(), toCreate);
        Item item = queryFindById.setParameter("id", itemDto.getId()).getSingleResult();
        assertNotNull(item);
        assertEquals(itemDto.getName(), item.getName());
        assertEquals(itemDto.getAvailable(), item.getAvailable());
        assertEquals(itemDto.getDescription(), item.getDescription());
    }

    @Test
    void createByNonExistingUserTesting() {
        ItemDto toCreate = ItemDto.builder()
                .name("Item1")
                .description("Some description")
                .available(true)
                .build();

        NotFoundException n =
                assertThrows(NotFoundException.class, () -> itemService.addItem(999, toCreate));
        assertEquals("Пользователь с id 999 не найден.", n.getMessage());
    }

    @Test
    void editItemTesting() {
        ItemDto itemDto = ItemDto.builder()
                .name("Item1")
                .description("Description1")
                .available(true)
                .build();

        ItemDto toUpdate = ItemDto.builder()
                .name("new Name")
                .description("new Description")
                .available(true)
                .build();

        ItemDto createdItemDto = itemService.addItem(user1.getId(), itemDto);
        ItemDto updated = itemService.editItem(user1.getId(), createdItemDto.getId(), toUpdate);
        Item item = queryFindById.setParameter("id", updated.getId()).getSingleResult();
        assertNotNull(createdItemDto);
        assertEquals(createdItemDto.getId(), item.getId());
        assertEquals(updated.getName(), item.getName());
        assertEquals(updated.getAvailable(), item.getAvailable());
        assertEquals(updated.getDescription(), item.getDescription());
    }

    @Test
    void editingItemNotByOwner() {
        String uniqueEmail1 = "user" + System.currentTimeMillis() + "@yandex.ru";
        String uniqueEmail2 = "user" + (System.currentTimeMillis() + 1) + "@yandex.ru";

        UserDto userDto1 = UserDto.builder()
                .name("User1")
                .email(uniqueEmail1)
                .build();

        UserDto userDto2 = UserDto.builder()
                .name("User2")
                .email(uniqueEmail2)
                .build();

        UserDto user1 = userService.addUser(userDto1);
        UserDto user2 = userService.addUser(userDto2);

        ItemDto itemDto = ItemDto.builder()
                .name("Item1")
                .description("Description1")
                .available(true)
                .build();

        ItemDto toUpdate = ItemDto.builder()
                .name("new Name")
                .description("new Description")
                .available(true)
                .build();

        ItemDto createdItemDto = itemService.addItem(user1.getId(), itemDto);

        AccessDeniedException a =
                assertThrows(AccessDeniedException.class, () -> itemService.editItem(user2.getId(), createdItemDto.getId(), toUpdate));
        assertEquals("Попытка несанкционированного доступа", a.getMessage());
    }

    @Test
    void editingByNotExistingUserTesting() {
        ItemDto itemDto = ItemDto.builder()
                .name("Item1")
                .description("Description1")
                .available(true)
                .build();

        ItemDto toUpdate = ItemDto.builder()
                .name("new Name")
                .description("new Description")
                .available(true)
                .build();

        ItemDto createdItemDto = itemService.addItem(user1.getId(), itemDto);
        AccessDeniedException a =
                assertThrows(AccessDeniedException.class, () -> itemService.editItem(2, createdItemDto.getId(), toUpdate));
        assertEquals("Попытка несанкционированного доступа", a.getMessage());
    }

    @Test
    void gettingItemsOfUserTesting() {
        List<ItemDto> foundItems = itemService.getItemsOfUser(user1.getId());
        List<ItemDto> foundItemsEmpty = itemService.getItemsOfUser(user3.getId());

        assertNotNull(foundItems);
        assertEquals(0, foundItemsEmpty.size());
        assertEquals(2, foundItems.size());
        ItemDto foundItem1 = foundItems.get(0);
        ItemDto foundItem2 = foundItems.get(1);
        assertEquals(item1.getName(), foundItem1.getName());
        assertEquals(item1.getAvailable(), foundItem1.getAvailable());
        assertEquals(item1.getDescription(), foundItem1.getDescription());
        assertEquals(item2.getName(), foundItem2.getName());
        assertEquals(item2.getAvailable(), foundItem2.getAvailable());
        assertEquals(item2.getDescription(), foundItem2.getDescription());
    }

    @Test
    void getItemTesting() {
        ItemDto foundItem = itemService.getItem(item1.getId());

        assertNotNull(foundItem);
        assertEquals(item1.getName(), foundItem.getName());
        assertEquals(item1.getAvailable(), foundItem.getAvailable());
        assertEquals(item1.getDescription(), foundItem.getDescription());

        NotFoundException n =
                assertThrows(NotFoundException.class, () -> itemService.getItem(999));
        assertEquals("Не найден предмет c id: 999", n.getMessage()); ;
    }

    @Test
    void deleteByIdTesting() {
        UserMapper userMapper = new UserMapper();
        ItemDto itemDto = ItemDto.builder()
                .name("Item1")
                .description("Description1")
                .available(true)
                .build();

        itemDto.setOwner(userMapper.toUser(user1));

        ItemDto createdItemDto = itemService.addItem(user1.getId(), itemDto);
        assertNotNull(itemService.getItem(createdItemDto.getId()));
        System.out.println(itemService.getItem(createdItemDto.getId()));

        NotFoundException f =
                assertThrows(NotFoundException.class, () -> itemService.deleteItem(999, createdItemDto.getId()));
        assertEquals("Пользователь с id 999 не найден.", f.getMessage());

    }

    @Test
    void deleteNotExistingItemTesting() {
        NotFoundException n =
                assertThrows(NotFoundException.class, () -> itemService.deleteItem(user1.getId(), 999));
        assertEquals("Не найден предмет c id: 999", n.getMessage());
    }
}
