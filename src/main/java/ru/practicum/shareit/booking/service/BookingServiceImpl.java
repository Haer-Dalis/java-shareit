package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public BookingOutputDto addBooking(Integer userId, BookingDto bookingDto) {
        log.info("Переданный userId: {}, itemId: {}", userId, bookingDto.getItemId());
        if (bookingDto.getItemId() == null) {
            throw new ValidationException("itemId не может не существовать");
        }
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Предмет с id " + bookingDto.getItemId() + " не найден"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        if (!item.getAvailable()) {
            throw new ValidationException(String.format("Айтем %s не может быть бронирован", item.getName()));
        }
        if (user.getId().equals(item.getOwner().getId())) {
            throw new NotFoundException("Вы пытаетесь забронировать вещь, которая Вам принадлежит!");
        }
        Booking booking = BookingMapper.toBooking(bookingDto);
        if (booking.getStart().isAfter(booking.getEnd()) || booking.getStart().isEqual(booking.getEnd())) {
            throw new ValidationException("Дата окончания брони неверна");
        }
        booking.setItem(item);
        booking.setBooker(user);
        return BookingMapper.toBookingOutDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingOutputDto approveBooking(Integer ownerId, Integer bookingId, Boolean approved) {
        Booking booking = getById(bookingId);
        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            throw new AccessDeniedException(
                    String.format("Пользователь %s не владелец предмета", ownerId));
        }
        userService.getUserById(ownerId);
        if (booking.getStatus().equals(Status.APPROVED)) {
            throw new ValidationException("Бронирование уже подтверждено");
        }
        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        bookingRepository.save(booking);
        return BookingMapper.toBookingOutDto(booking);
    }

    @Override
    public BookingOutputDto getBooking(Integer userId, Integer bookingId) {
        userService.getUserById(userId);
        Booking booking = getById(bookingId);
        boolean isBooker = booking.getBooker().getId().equals(userId);
        boolean isOwner = booking.getItem().getOwner().getId().equals(userId);
        if (!isBooker && !isOwner) {
            throw new NotFoundException("Попытка несанкционированного доступа к бронированию!");
        }
        return BookingMapper.toBookingOutDto(booking);
    }

    @Override
    public List<BookingOutputDto> getAllUserBookings(Integer userId, String state) {
        userService.getUserById(userId);
        State bookingState = State.getEnumValue(state);
        LocalDateTime currentTime = LocalDateTime.now();
        List<Booking> bookings;
        switch (bookingState) {
            case ALL:
                bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartAsc(userId, currentTime, currentTime);
                break;
            case PAST:
                bookings = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, currentTime);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, currentTime);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.REJECTED);
                break;
            default:
                throw new IllegalArgumentException("Некорректный статус бронирования: " + state);
        }
        return bookings.stream()
                .map(BookingMapper::toBookingOutDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingOutputDto> getAllOwnerItemBookings(Integer userId, String state) {
        userService.getUserById(userId);
        State bookingState = State.getEnumValue(state);
        LocalDateTime currentTime = LocalDateTime.now();
        List<Booking> bookings;
        switch (bookingState) {
            case ALL:
                bookings = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartAsc(userId, currentTime, currentTime);
                break;
            case PAST:
                bookings = bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(userId, currentTime);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(userId, currentTime);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, Status.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, Status.REJECTED);
                break;
            default:
                throw new IllegalArgumentException("Некорректный статус бронирования: " + state);
        }
        return bookings.stream()
                .map(BookingMapper::toBookingOutDto)
                .collect(Collectors.toList());
    }

    @Override
    public Booking getById(Integer id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Не найдено бронирование c id: %s", id)));
    }
}
