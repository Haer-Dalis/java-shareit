package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentOutputDto;
import ru.practicum.shareit.item.dto.ItemOutputDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingInfoTests {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    private Item item;
    private User owner;
    private User otherUser;
    private ItemOutputDto itemOutputDto;

    @BeforeEach
    void setUp() {
        owner = User.builder()
                .id(1)
                .name("Owner")
                .email("owner@example.com")
                .build();

        otherUser = User.builder()
                .id(2)
                .name("Other User")
                .email("other@example.com")
                .build();

        item = new Item(1, "Test Item", "Description", true, owner, null);

        itemOutputDto = ItemOutputDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    @Test
    void throwNotFoundExceptionWhenItemDoesNotExist() {
        when(itemRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.addBookingInfoAndComments(1, 1));
    }

    @Test
    void returnItemWithoutBookingsAndCommentsForNonOwner() {
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemId(item.getId())).thenReturn(Collections.emptyList());

        ItemOutputDto result = itemService.addBookingInfoAndComments(item.getId(), otherUser.getId());

        assertNull(result.getLastBooking());
        assertNull(result.getNextBooking());
        assertEquals(Collections.emptyList(), result.getComments());
    }

    @Test
    void returnItemWithLastAndNextBookingForOwnerTesting() {
        Booking lastBooking = new Booking(1, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), item, owner, Status.APPROVED);
        Booking nextBooking = new Booking(2, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), item, owner, Status.APPROVED);
        BookingShortDto lastBookingDto = new BookingShortDto(1, owner.getId(), lastBooking.getStart(), lastBooking.getEnd());
        BookingShortDto nextBookingDto = new BookingShortDto(2, owner.getId(), nextBooking.getStart(), nextBooking.getEnd());

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(eq(item.getId()), eq(Status.APPROVED), any()))
                .thenReturn(Optional.of(lastBooking));
        when(bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(eq(item.getId()), eq(Status.APPROVED), any()))
                .thenReturn(Optional.of(nextBooking));

        ItemOutputDto result = itemService.addBookingInfoAndComments(item.getId(), owner.getId());

        assertEquals(lastBookingDto, result.getLastBooking());
        assertEquals(nextBookingDto, result.getNextBooking());
    }

    @Test
    void returnItemWithOnlyLastBookingWhenNoNextBookingExistsTesting() {
        Booking lastBooking = new Booking(1, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), item, owner, Status.APPROVED);
        BookingShortDto lastBookingDto = new BookingShortDto(1, owner.getId(), lastBooking.getStart(), lastBooking.getEnd());

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(eq(item.getId()), eq(Status.APPROVED), any()))
                .thenReturn(Optional.of(lastBooking));
        when(bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(eq(item.getId()), eq(Status.APPROVED), any()))
                .thenReturn(Optional.empty());

        ItemOutputDto result = itemService.addBookingInfoAndComments(item.getId(), owner.getId());

        assertEquals(lastBookingDto, result.getLastBooking());
        assertNull(result.getNextBooking());
    }

    @Test
    void returnItemWithOnlyNextBookingWhenNoLastBookingExistsTesting() {
        Booking nextBooking = new Booking(2, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), item, owner, Status.APPROVED);
        BookingShortDto nextBookingDto = new BookingShortDto(2, owner.getId(), nextBooking.getStart(), nextBooking.getEnd());

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(eq(item.getId()), eq(Status.APPROVED), any()))
                .thenReturn(Optional.empty());
        when(bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(eq(item.getId()), eq(Status.APPROVED), any()))
                .thenReturn(Optional.of(nextBooking));

        ItemOutputDto result = itemService.addBookingInfoAndComments(item.getId(), owner.getId());

        assertNull(result.getLastBooking());
        assertEquals(nextBookingDto, result.getNextBooking());
    }

    @Test
    void returnItemWithCommentsTesting() {
        Comment comment = new Comment(1, "Great item!", item, otherUser, LocalDateTime.now());
        CommentOutputDto commentOutputDto = new CommentOutputDto(1, "Great item!", "Other User", comment.getCreated());

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemId(item.getId())).thenReturn(List.of(comment));

        ItemOutputDto result = itemService.addBookingInfoAndComments(item.getId(), otherUser.getId());

        assertEquals(1, result.getComments().size());
        assertEquals(commentOutputDto, result.getComments().get(0));
    }
}
