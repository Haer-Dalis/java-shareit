package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
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

@Slf4j
@Component
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public Booking addBooking(Integer userId, Integer itemId, Booking booking) {
        log.info("Переданный userId: {}, itemId: {}", userId, itemId);
        if (itemId == null) {
            throw new ValidationException("itemId не может не существовать");
        }
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет с id " + itemId + " не найден"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        if (!item.getAvailable()) {
            throw new ValidationException(String.format("Айтем %s не может быть бронирован", item.getName()));
        }
        if (user.getId().equals(item.getOwner().getId())) {
            throw new NotFoundException("Вы пытаетесь забронировать вещь, которая Вам принадлежит!");
        }
        if (booking.getStart().isAfter(booking.getEnd()) || booking.getStart().isEqual(booking.getEnd())) {
            throw new ValidationException("Дата окончания брони неверна");
        }
        booking.setItem(item);
        booking.setBooker(user);
        return bookingRepository.save(booking);
    }

    @Override
    public Booking approveBooking(Integer ownerId, Integer bookingId, Boolean approved) {
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
        return booking;
    }

    @Override
    public Booking getBooking(Integer userId, Integer bookingId) {
        userService.getUserById(userId);
        Booking booking = getById(bookingId);
        boolean isBooker = booking.getBooker().getId().equals(userId);
        boolean isOwner = booking.getItem().getOwner().getId().equals(userId);
        if (!isBooker && !isOwner) {
            throw new NotFoundException("Попытка несанкционированного доступа к бронированию!");
        }
        return booking;
    }

    @Override
    public List<Booking> getAllUserBookings(Integer userId, String state) {
        userService.getUserById(userId);
        State bookingState = State.getEnumValue(state);
        LocalDateTime currentTime = LocalDateTime.now();
        switch (bookingState) {
            case ALL:
                return bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
            case CURRENT:
                return bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartAsc(userId, currentTime, currentTime);
            case PAST:
                return bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, currentTime);
            case FUTURE:
                return bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, currentTime);
            case WAITING:
                return bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.WAITING);
            case REJECTED:
                return bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.REJECTED);
            default:
                throw new IllegalArgumentException("Некорректный статус бронирования: " + state);
        }
    }

    @Override
    public List<Booking> getAllOwnerItemBookings(Integer userId, String state) {
        userService.getUserById(userId);
        State bookingState = State.getEnumValue(state);
        LocalDateTime currentTime = LocalDateTime.now();
        switch (bookingState) {
            case ALL:
                return bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId);
            case CURRENT:
                return bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartAsc(userId, currentTime, currentTime);
            case PAST:
                return bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(userId, currentTime);
            case FUTURE:
                return bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(userId, currentTime);
            case WAITING:
                return bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, Status.WAITING);
            case REJECTED:
                return bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, Status.REJECTED);
            default:
                throw new IllegalArgumentException("Некорректный статус бронирования: " + state);
        }
    }

    @Override
    public Booking getById(Integer id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Не найдено бронирование c id: %s", id)));
    }
}
