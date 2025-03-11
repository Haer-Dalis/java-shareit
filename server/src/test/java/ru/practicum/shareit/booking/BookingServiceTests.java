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
import ru.practicum.shareit.exception.AccessDeniedException;
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
import static org.mockito.ArgumentMatchers.eq;
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
    private Booking currentBooking;
    private Booking pastBooking;
    private Booking futureBooking;
    private Booking waitingBooking;
    private Booking rejectedBooking;

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
        LocalDateTime now = LocalDateTime.now();
        currentBooking = new Booking(1, now.minusDays(1), now.plusDays(1), item1, user1, Status.APPROVED);
        pastBooking = new Booking(2, now.minusDays(10), now.minusDays(5), item1, user1, Status.APPROVED);
        futureBooking = new Booking(3, now.plusDays(5), now.plusDays(10), item1, user1, Status.APPROVED);
        waitingBooking = new Booking(4, now.plusDays(1), now.plusDays(2), item1, user1, Status.WAITING);
        rejectedBooking = new Booking(5, now.plusDays(3), now.plusDays(4), item1, user1, Status.REJECTED);
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
    void approveBookingAlreadyApproved() {
        booking.setStatus(Status.APPROVED);
        when(bookingRepository.findById(1)).thenReturn(Optional.of(booking));
        assertThrows(ValidationException.class, () -> bookingService.approveBooking(user1.getId(), 1, true));
    }

    @Test
    void approveBookingAccessDenied() {
        when(bookingRepository.findById(1)).thenReturn(Optional.of(booking));
        assertThrows(AccessDeniedException.class, () -> bookingService.approveBooking(user2.getId(), 1, true));
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
    void getAllOwnerItemBookingsAllTesting() {
        when(userService.getUserById(user1.getId())).thenReturn(UserMapper.toUserDto(user1));
        when(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(user1.getId())).thenReturn(List.of(booking));

        List<BookingOutputDto> result = bookingService.getAllOwnerItemBookings(user1.getId(), "ALL");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(booking.getId(), result.get(0).getId());
    }

    @Test
    void getAllOwnerItemBookingsInvalidStateTesting() {
        when(userService.getUserById(user1.getId())).thenReturn(UserMapper.toUserDto(user1));

        assertThrows(UnknownValueException.class, () -> bookingService.getAllOwnerItemBookings(user1.getId(), "INVALID_STATE"));
    }

    @Test
    void getAllUserBookingsCurrentTesting() {
        LocalDateTime currentTime = LocalDateTime.now();
        booking.setStart(currentTime.minusDays(1));
        booking.setEnd(currentTime.plusDays(1));

        when(userService.getUserById(user2.getId())).thenReturn(UserMapper.toUserDto(user2));
        when(bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartAsc(eq(user2.getId()), any(), any()))
                .thenReturn(List.of(booking));

        List<BookingOutputDto> result = bookingService.getAllUserBookings(user2.getId(), "CURRENT");

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getAllOwnerItemBookingsCurrentTesting() {
        LocalDateTime currentTime = LocalDateTime.now();
        booking.setStart(currentTime.minusDays(1));
        booking.setEnd(currentTime.plusDays(1));

        when(userService.getUserById(user1.getId())).thenReturn(UserMapper.toUserDto(user1));
        when(bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartAsc(eq(user1.getId()), any(), any()))
                .thenReturn(List.of(booking));

        List<BookingOutputDto> result = bookingService.getAllOwnerItemBookings(user1.getId(), "CURRENT");

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void addBookingWithNullItemIdThrowsValidationException() {
        BookingDto bookingDto = new BookingDto(null, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), Status.WAITING);

        assertThrows(ValidationException.class, () -> bookingService.addBooking(user1.getId(), bookingDto));
    }

    @Test
    void addBookingWhenItemNotAvailableThrowsNotFoundExceptionTesting() {
        item1.setAvailable(false);
        when(itemRepository.findById(item1.getId())).thenReturn(Optional.of(item1));

        BookingDto bookingDto = new BookingDto(item1.getId(), LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), Status.WAITING);

        assertThrows(NotFoundException.class, () -> bookingService.addBooking(user2.getId(), bookingDto));
    }

    @Test
    void addBookingByOwnerThrowsNotFoundExceptionTesting() {
        when(itemRepository.findById(item1.getId())).thenReturn(Optional.of(item1));

        BookingDto bookingDto = new BookingDto(item1.getId(), LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), Status.WAITING);

        assertThrows(NotFoundException.class, () -> bookingService.addBooking(user1.getId(), bookingDto));
    }

    @Test
    void approveBookingWithFalseRejectsBookingTesting() {
        booking.setStatus(Status.WAITING);
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(booking);

        BookingOutputDto updatedBooking = bookingService.approveBooking(user1.getId(), booking.getId(), false);

        assertEquals(Status.REJECTED, updatedBooking.getStatus());
    }

    @Test
    void getAllUserBookingsByStateTesting() {
        when(userService.getUserById(user1.getId())).thenReturn(UserMapper.toUserDto(user1));

        when(bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(eq(user1.getId()), any()))
                .thenReturn(List.of(booking));
        List<BookingOutputDto> pastBookings = bookingService.getAllUserBookings(user1.getId(), "PAST");
        assertEquals(1, pastBookings.size());

        when(bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(eq(user1.getId()), any()))
                .thenReturn(List.of());
        List<BookingOutputDto> futureBookings = bookingService.getAllUserBookings(user1.getId(), "FUTURE");
        assertTrue(futureBookings.isEmpty());
    }

    @Test
    void getAllOwnerItemBookingsByStateTesting() {
        when(userService.getUserById(user1.getId())).thenReturn(UserMapper.toUserDto(user1));

        when(bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(eq(user1.getId()), any()))
                .thenReturn(List.of(booking));
        List<BookingOutputDto> pastBookings = bookingService.getAllOwnerItemBookings(user1.getId(), "PAST");
        assertEquals(1, pastBookings.size());

        when(bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartAsc(eq(user1.getId()), any(), any()))
                .thenReturn(List.of());
        List<BookingOutputDto> currentBookings = bookingService.getAllOwnerItemBookings(user1.getId(), "CURRENT");
        assertTrue(currentBookings.isEmpty());
    }

    @Test
    void getAllUserBookingsByStateAllTesting() {
        when(userService.getUserById(user1.getId())).thenReturn(UserMapper.toUserDto(user1));
        when(bookingRepository.findAllByBookerIdOrderByStartDesc(user1.getId())).thenReturn(List.of(booking));

        List<BookingOutputDto> allBookings = bookingService.getAllUserBookings(user1.getId(), "ALL");
        assertEquals(1, allBookings.size());
    }

    @Test
    void getAllUserBookingsByStateWaitingTesting() {
        when(userService.getUserById(user1.getId())).thenReturn(UserMapper.toUserDto(user1));
        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(user1.getId(), Status.WAITING))
                .thenReturn(List.of(booking));

        List<BookingOutputDto> waitingBookings = bookingService.getAllUserBookings(user1.getId(), "WAITING");
        assertEquals(1, waitingBookings.size());
    }

    @Test
    void getAllUserBookingsByStateRejectedTesting() {
        when(userService.getUserById(user1.getId())).thenReturn(UserMapper.toUserDto(user1));
        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(user1.getId(), Status.REJECTED))
                .thenReturn(List.of(booking));

        List<BookingOutputDto> rejectedBookings = bookingService.getAllUserBookings(user1.getId(), "REJECTED");
        assertEquals(1, rejectedBookings.size());
    }

    @Test
    void getAllUserBookingsByStateThrowsExceptionTesting() {
        when(userService.getUserById(user1.getId())).thenReturn(UserMapper.toUserDto(user1));

        Exception exception = assertThrows(UnknownValueException.class,
                () -> bookingService.getAllUserBookings(user1.getId(), "INVALID_STATE"));
    }

    @Test
    void getAllOwnerItemBookingsByStateAllTesting() {
        when(userService.getUserById(item1.getOwner().getId())).thenReturn(UserMapper.toUserDto(item1.getOwner()));
        when(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(item1.getOwner().getId())).thenReturn(List.of(booking));

        List<BookingOutputDto> allBookings = bookingService.getAllOwnerItemBookings(item1.getOwner().getId(), "ALL");
        assertEquals(1, allBookings.size());
    }

    @Test
    void getAllOwnerItemBookingsByStateWaitingTesting() {
        when(userService.getUserById(item1.getOwner().getId())).thenReturn(UserMapper.toUserDto(item1.getOwner()));
        when(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(item1.getOwner().getId(), Status.WAITING))
                .thenReturn(List.of(booking));

        List<BookingOutputDto> waitingBookings = bookingService.getAllOwnerItemBookings(item1.getOwner().getId(), "WAITING");
        assertEquals(1, waitingBookings.size());
    }

    @Test
    void getAllOwnerItemBookingsByStateThrowsExceptionTesting() {
        when(userService.getUserById(item1.getOwner().getId())).thenReturn(UserMapper.toUserDto(item1.getOwner()));

        Exception exception = assertThrows(UnknownValueException.class,
                () -> bookingService.getAllOwnerItemBookings(item1.getOwner().getId(), "INVALID_STATE"));
    }

    @Test
    void getAllOwnerItemBookingsByStateRejectedTesting() {
        when(userService.getUserById(item1.getOwner().getId())).thenReturn(UserMapper.toUserDto(item1.getOwner()));
        when(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(item1.getOwner().getId(), Status.REJECTED))
                .thenReturn(List.of(booking));

        List<BookingOutputDto> rejectedBookings = bookingService.getAllOwnerItemBookings(item1.getOwner().getId(), "REJECTED");
        assertEquals(1, rejectedBookings.size());
    }

    @Test
    void getAllUserBookingsPastTesting() {
        when(bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(eq(user1.getId()), any()))
                .thenReturn(List.of(pastBooking));

        List<BookingOutputDto> result = bookingService.getAllUserBookings(user1.getId(), "PAST");

        assertEquals(1, result.size());
        assertEquals(pastBooking.getId(), result.get(0).getId());
    }

    @Test
    void getAllUserBookingsFutureTesting() {
        when(bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(eq(user1.getId()), any()))
                .thenReturn(List.of(futureBooking));

        List<BookingOutputDto> result = bookingService.getAllUserBookings(user1.getId(), "FUTURE");

        assertEquals(1, result.size());
        assertEquals(futureBooking.getId(), result.get(0).getId());
    }

    @Test
    void getAllUserBookingsWaitingTesting() {
        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(user1.getId(), Status.WAITING))
                .thenReturn(List.of(waitingBooking));

        List<BookingOutputDto> result = bookingService.getAllUserBookings(user1.getId(), "WAITING");

        assertEquals(1, result.size());
        assertEquals(waitingBooking.getId(), result.get(0).getId());
    }

    @Test
    void getAllUserBookingsRejectedTesting() {
        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(user1.getId(), Status.REJECTED))
                .thenReturn(List.of(rejectedBooking));

        List<BookingOutputDto> result = bookingService.getAllUserBookings(user1.getId(), "REJECTED");

        assertEquals(1, result.size());
        assertEquals(rejectedBooking.getId(), result.get(0).getId());
    }
}


