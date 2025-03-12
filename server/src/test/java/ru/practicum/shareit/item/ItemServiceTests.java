package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentOutputDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
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

@ExtendWith(MockitoExtension.class)
class ItemServiceTests {
    @Mock private ItemRepository itemRepository;
    @Mock private UserRepository userRepository;
    @Mock private BookingRepository bookingRepository;
    @Mock private CommentRepository commentRepository;
    @Mock private UserService userService;
    @Mock private ItemMapper itemMapper;
    @Mock private UserMapper userMapper;
    @Mock private ItemRequestRepository itemRequestRepository;

    @InjectMocks private ItemServiceImpl itemService;

    private User user;
    private Item item;
    private ItemDto itemDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1);

        item = new Item();
        item.setId(1);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(user);

        itemDto = ItemDto.builder()
                .id(1)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .owner(user)
                .build();
    }

    @Test
    void addItemTesting() {
        when(userService.getUserById(user.getId())).thenReturn(UserDto.builder().id(user.getId()).build());
        when(userMapper.toUser(any())).thenReturn(user);
        when(itemMapper.toItem(any())).thenReturn(item);
        when(itemRepository.save(any())).thenReturn(item);
        when(itemMapper.toItemDto(any())).thenReturn(itemDto);

        ItemDto result = itemService.addItem(user.getId(), itemDto);
        assertNotNull(result);
        assertEquals("Test Item", result.getName());
    }

    @Test
    void createByNonExistingUserTesting() {
        when(userService.getUserById(user.getId())).thenThrow(new NotFoundException("User not found"));
        assertThrows(NotFoundException.class, () -> itemService.addItem(user.getId(), itemDto));
    }

    @Test
    void editItemTesting() {
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(userService.getUserById(user.getId())).thenReturn(UserDto.builder().id(user.getId()).build());
        Item updatedItem = Item.builder()
                .id(item.getId())
                .name("Updated Item")
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(item.getOwner())
                .request(item.getRequest())
                .build();

        when(itemRepository.save(any(Item.class))).thenReturn(updatedItem);
        when(itemMapper.toItemDto(any())).thenReturn(ItemDto.builder()
                .id(updatedItem.getId())
                .name(updatedItem.getName())
                .description(updatedItem.getDescription())
                .available(updatedItem.getAvailable())
                .owner(updatedItem.getOwner())
                .request(null)
                .requestId(updatedItem.getRequest() != null ? updatedItem.getRequest().getId() : null)
                .build());

        ItemDto updatedItemDto = ItemDto.builder()
                .id(itemDto.getId())
                .name("Updated Item")
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(itemDto.getOwner())
                .request(itemDto.getRequest())
                .requestId(itemDto.getRequestId())
                .build();

        ItemDto result = itemService.editItem(user.getId(), item.getId(), updatedItemDto);

        assertEquals("Updated Item", result.getName());
    }

    @Test
    void editingItemNotByOwner() {
        User anotherUser = new User();
        anotherUser.setId(2);
        item.setOwner(anotherUser);

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(userService.getUserById(user.getId())).thenReturn(UserDto.builder().id(user.getId()).build());

        assertThrows(AccessDeniedException.class, () -> itemService.editItem(user.getId(), item.getId(), itemDto));
    }

    @Test
    void gettingItemsOfUserTesting() {
        when(userService.getUserById(user.getId())).thenReturn(UserDto.builder().id(user.getId()).build());
        when(itemRepository.findByOwnerId(user.getId())).thenReturn(List.of(item));
        when(itemMapper.toItemDto(any())).thenReturn(itemDto);

        List<ItemDto> result = itemService.getItemsOfUser(user.getId());
        assertFalse(result.isEmpty());
    }

    @Test
    void getItemTesting() {
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(itemMapper.toItemDto(any())).thenReturn(itemDto);

        ItemDto result = itemService.getItem(item.getId());
        assertNotNull(result);
        assertEquals("Test Item", result.getName());
    }

    @Test
    void deleteItemByNonOwnerTesting() {
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(userService.getUserById(user.getId())).thenReturn(UserDto.builder().id(user.getId()).build());

        assertThrows(NullPointerException.class, () -> itemService.deleteItem(user.getId(), item.getId()));
    }

    @Test
    void addCommentWithoutBookingTesting() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.findFirstByItemIdAndBookerIdAndStatusAndEndBefore(eq(item.getId()), eq(user.getId()), eq(Status.APPROVED), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());

        CommentDto commentDto = CommentDto.builder().text("Nice item!").build();
        assertThrows(ValidationException.class, () -> itemService.addComment(user.getId(), commentDto, item.getId()));
    }

    @Test
    void addCommentTesting() {
        // Подготовка данных
        Integer userId = 1;
        Integer itemId = 2;
        CommentDto commentDto = new CommentDto("Great item!");

        User user = new User();
        user.setId(userId);
        Item item = new Item();
        item.setId(itemId);
        Booking booking = new Booking(1, LocalDateTime.now().minusDays(1), LocalDateTime.now().minusDays(2),
                item, user, Status.APPROVED);
        Comment comment = new Comment();
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setText(commentDto.getText());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.findFirstByItemIdAndBookerIdAndStatusAndEndBefore(
                eq(itemId), eq(userId), eq(Status.APPROVED), any(LocalDateTime.class)))
                .thenReturn(Optional.of(booking));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        // Выполнение метода
        CommentOutputDto result = itemService.addComment(userId, commentDto, itemId);

        // Проверка результата
        assertNotNull(result);
        assertEquals(commentDto.getText(), result.getText());
    }

    @Test
    void editingByNotExistingUserTesting() {
        when(userService.getUserById(anyInt())).thenThrow(new NotFoundException("User not found"));

        assertThrows(NotFoundException.class, () -> itemService.editItem(999, 1, itemDto));
    }

    @Test
    void getByRequestIdTesting() {
        when(itemRepository.findAllByRequestId(anyInt())).thenReturn(Collections.singletonList(item));

        List<Item> result = itemService.getByRequestId(1);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void addCommentUserNotFoundThrowNotFoundExceptionTesting() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                itemService.addComment(999, new CommentDto("Nice!"), item.getId()));
    }

    @Test
    void searchItemsWithQuery() {
        when(itemRepository.search(anyString())).thenReturn(Collections.singletonList(item));
        when(itemMapper.toItemDto(any())).thenReturn(itemDto);

        List<ItemDto> result = itemService.search("Test");

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void searchItemsBlankTextTesting() {
        List<ItemDto> result = itemService.search("");

        assertTrue(result.isEmpty());
    }
}

