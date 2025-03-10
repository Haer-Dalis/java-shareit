package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnknownValueException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceTests {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @InjectMocks
    private BookingServiceImpl bookingService;

    private User user1;
    private User user2;
    private Item item1;
    private Booking booking;
    private BookingDto bookingInputDto;
    private BookingOutputDto bookingOutputDto;

    @BeforeEach
    void setUp() {
        user1 = new User(1, "Ivan", "ivan@durnoiservis.com");
        user2 = new User(2, "Petr", "petr@durnoiservis.com");

        item1 = Item.builder()
                .id(1)
                .name("Item1")
                .description("Description")
                .available(true)
                .owner(user1)
                .request(null)
                .build();

        booking = new Booking(
                1,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                item1,
                user2,
                Status.WAITING
        );

        bookingInputDto = new BookingDto(
                item1.getId(),
                booking.getStart(),
                booking.getEnd(),
                Status.APPROVED
        );

        bookingOutputDto = new BookingOutputDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus(),
                UserMapper.toUserDto(user2),
                ItemMapper.toItemOutputDto(item1)
        );
    }

    @Test
    void addBookingTesting() {
        when(userRepository.findById(user2.getId())).thenReturn(Optional.of(user2));
        when(itemRepository.findById(item1.getId())).thenReturn(Optional.of(item1));
        when(bookingRepository.save(any())).thenReturn(booking);

        BookingOutputDto created = bookingService.addBooking(user2.getId(), bookingInputDto);

        assertNotNull(created);
        assertEquals(bookingInputDto.getStart(), created.getStart());
        assertEquals(bookingInputDto.getEnd(), created.getEnd());
    }

    @Test
    void addBookingWithInvalidDatesTesting() {
        when(userRepository.findById(user2.getId())).thenReturn(Optional.of(user2));
        when(itemRepository.findById(item1.getId())).thenReturn(Optional.of(item1));
        bookingInputDto.setEnd(bookingInputDto.getStart().minusDays(2));
        assertThrows(ValidationException.class, () ->
                bookingService.addBooking(user2.getId(), bookingInputDto));
    }

    @Test
    void approveBookingTesting() {
        when(bookingRepository.findById(1)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(booking);

        BookingOutputDto approved = bookingService.approveBooking(user1.getId(), 1, true);

        assertNotNull(approved);
        assertEquals(Status.APPROVED, approved.getStatus());
    }

    @Test
    void getByIdTesting() {
        when(bookingRepository.findById(1)).thenReturn(Optional.of(booking));

        Booking found = bookingService.getById(1);

        assertNotNull(found);
        assertEquals(1, found.getId());
        assertEquals(item1, found.getItem());
        assertEquals(user2, found.getBooker());
        assertEquals(booking.getStart(), found.getStart());
        assertEquals(booking.getEnd(), found.getEnd());
        assertEquals(Status.WAITING, found.getStatus());
    }

    @Test
    void getByIdNotFoundTesting() {
        when(bookingRepository.findById(user2.getId())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> bookingService.getById(user2.getId()));
    }

    @Test
    void getBookingSuccessTesting() {
        when(userService.getUserById(user2.getId())).thenReturn(UserMapper.toUserDto(user2));
        when(bookingRepository.findById(1)).thenReturn(Optional.of(booking));

        BookingOutputDto result = bookingService.getBooking(user2.getId(), 1);

        assertNotNull(result);
        assertEquals(booking.getId(), result.getId());
        assertEquals(booking.getStart(), result.getStart());
        assertEquals(booking.getEnd(), result.getEnd());
        assertEquals(booking.getStatus(), result.getStatus());
    }

    @Test
    void getAllUserBookingsAllTesting() {
        when(userService.getUserById(user2.getId())).thenReturn(UserMapper.toUserDto(user2));
        when(bookingRepository.findAllByBookerIdOrderByStartDesc(user2.getId())).thenReturn(List.of(booking));

        List<BookingOutputDto> result = bookingService.getAllUserBookings(user2.getId(), "ALL");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(booking.getId(), result.get(0).getId());
    }

    @Test
    void getAllUserBookingsInvalidStateTesting() {
        when(userService.getUserById(user2.getId())).thenReturn(UserMapper.toUserDto(user2));
        assertThrows(UnknownValueException.class, () -> bookingService.getAllUserBookings(user2.getId(), "INVALID_STATE"));
    }

    @Test
    void getAllOwnerItemBookingsAll() {
        when(userService.getUserById(user1.getId())).thenReturn(UserMapper.toUserDto(user1));
        when(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(user1.getId())).thenReturn(List.of(booking));

        List<BookingOutputDto> result = bookingService.getAllOwnerItemBookings(user1.getId(), "ALL");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(booking.getId(), result.get(0).getId());
    }

    @Test
    void getAllOwnerItemBookingsInvalidState() {
        when(userService.getUserById(user1.getId())).thenReturn(UserMapper.toUserDto(user1));

        assertThrows(UnknownValueException.class, () -> bookingService.getAllOwnerItemBookings(user1.getId(), "INVALID_STATE"));
    }
}


