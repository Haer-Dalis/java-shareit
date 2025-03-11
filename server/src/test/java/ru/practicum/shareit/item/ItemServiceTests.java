package ru.practicum.shareit.item;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.ShareItApp;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = ShareItApp.class)
class ItemServiceTests {

    @Autowired
    private EntityManager em;
    private UserDto user1;
    private UserDto user2;
    private UserDto user3;
    UserDto userDto1;
    ItemDto itemDto1;
    private ItemDto item1;
    private ItemDto item2;
    private ItemDto item3;
    private Item item;
    private TypedQuery<Item> queryFindById;

    @Mock
    private ItemRepository itemRepository;


    @Autowired
    private UserService userService;
    @Autowired
    private BookingService bookingService;
    @Autowired
    private ItemServiceImpl itemService;
    private UserMapper userMapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;

    ItemServiceTests() {
    }

    @BeforeEach
    void setUp() {
        userMapper = new UserMapper();
        String uniqueEmail1 = "ivan" + System.currentTimeMillis() + "@yandex.ru";
        String uniqueEmail2 = "petr" + (System.currentTimeMillis() + 1) + "@yandex.ru";
        String uniqueEmail3 = "sidor" + (System.currentTimeMillis() + 2) + "@yandex.ru";

        userDto1 = UserDto.builder()
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

        itemDto1 = ItemDto.builder()
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

        item = Item.builder()
                .name("Item4")
                .description("Description4")
                .available(true)
                .build();

        itemRepository = Mockito.mock(ItemRepository.class);
        userRepository = Mockito.mock(UserRepository.class);

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
        assertEquals("Не найден предмет c id: 999", n.getMessage());
    }

    @Test
    void deleteItemByNonOwnerTesting() {
        NullPointerException n =
                assertThrows(NullPointerException.class, () -> itemService.deleteItem(user2.getId(), item1.getId()));
    }

    @Test
    void addCommentWithoutBookingTesting() {
        CommentDto commentDto = CommentDto.builder()
                .text("Great item!")
                .build();

        ValidationException n =
                assertThrows(ValidationException.class, () -> itemService.addComment(user3.getId(), commentDto, item1.getId()));
        assertEquals("Пользователь 9 не бронировал этот предмет", n.getMessage());
    }

    @Test
    void getByRequestIdTesting() {
        List<Item> items = itemService.getByRequestId(user1.getId());

        assertNotNull(items);
        assertEquals(0, items.size());
    }

    @Test
    void addCommentwhenUserNotFoundthenThrowNotFoundExceptionTesting() {
        Integer userId = 1;
        Integer itemId = 1;
        CommentDto commentDto = new CommentDto("Nice item!");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ValidationException exception = assertThrows(ValidationException.class, () ->
                itemService.addComment(userId, commentDto, itemId));

        assertEquals("Пользователь " + userId + " не бронировал этот предмет", exception.getMessage());
    }

    @Test
    void addComment_whenItemNotFoundThrowNotFoundExceptionTesting() {
        Integer userId = 1;
        Integer itemId = 1;
        CommentDto commentDto = new CommentDto("Nice item!");
        User user = new User();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        ValidationException exception = assertThrows(ValidationException.class, () ->
                itemService.addComment(userId, commentDto, itemId));

        assertEquals("Пользователь " + itemId + " не бронировал этот предмет", exception.getMessage());
    }

    @Test
    void addCommentBookingNotFoundThrowValidationExceptionTesting() {
        Integer userId = 1;
        Integer itemId = 1;
        CommentDto commentDto = new CommentDto("Nice item!");
        User user = new User();
        Item item = new Item();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.findFirstByItemIdAndBookerIdAndStatusAndEndBefore(eq(itemId), eq(userId), eq(Status.APPROVED), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());

        ValidationException exception = assertThrows(ValidationException.class, () ->
                itemService.addComment(userId, commentDto, itemId));

        assertEquals("Пользователь " + userId + " не бронировал этот предмет", exception.getMessage());
    }

    @Test
    void getItemsOfUserUserExistsReturnItemListTesting() {
        Integer userId = 1;
        User user = new User();
        Item item1 = new Item(1, "Item1", "Description1", true, user, null);
        Item item2 = new Item(2, "Item2", "Description2", true, user, null);
        List<Item> items = List.of(item1, item2);

        when(itemRepository.findByOwnerId(userId)).thenReturn(items);

        List<ItemDto> result = itemService.getItemsOfUser(userId);

        assertEquals(2, result.size());
        assertEquals("Item1", result.get(0).getName());
        assertEquals("Item2", result.get(1).getName());
    }

    @Test
    void getItemById_ExistingItem() {
        when(itemRepository.findById(1)).thenReturn(Optional.of(item));
        ItemDto foundItem = itemService.getItem(1);
        assertNotNull(foundItem);
        assertEquals("Item1", foundItem.getName());
    }

    @Test
    void getUserItems_ExistingUser() {
        List<Item> items = List.of(item);
        when(itemRepository.findByOwnerId(1)).thenReturn(items);
        List<ItemDto> userItems = itemService.getItemsOfUser(1);
        assertFalse(userItems.isEmpty());
    }

    @Test
    void searchItems_WithQuery() {
        List<Item> items = List.of(item);
        when(itemRepository.search("Item1")).thenReturn(items);
        List<ItemDto> result = itemService.search("Item1");
        assertFalse(result.isEmpty());
    }

    @Test
    void searchItems_EmptyQuery() {
        List<ItemDto> result = itemService.search("");
        assertTrue(result.isEmpty());
    }

    @Test
    void testItemDeletionByNonOwner() {
        when(itemRepository.findById(1)).thenReturn(Optional.of(item));
        assertThrows(NullPointerException.class, () -> itemService.deleteItem(1, 2));
    }

    @Test
    void testGetExistingItem() {
        when(itemRepository.findById(1)).thenReturn(Optional.of(item));
        ItemDto foundItem = itemService.getItem(1);
        assertNotNull(foundItem);
    }
}

