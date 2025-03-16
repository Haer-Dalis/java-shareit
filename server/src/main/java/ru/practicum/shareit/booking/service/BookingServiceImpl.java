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
        log.info("Начало метода addBooking. Переданный userId: {}, itemId: {}", userId, bookingDto.getItemId());

        if (bookingDto.getItemId() == null) {
            log.error("Ошибка валидации: itemId не может быть null");
            throw new ValidationException("itemId не может не существовать");
        }

        log.info("Поиск предмета с id: {}", bookingDto.getItemId());
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> {
                    log.error("Ошибка: предмет с id {} не найден", bookingDto.getItemId());
                    return new NotFoundException("Предмет с id " + bookingDto.getItemId() + " не найден");
                });

        log.info("Поиск пользователя с id: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("Ошибка: пользователь с id {} не найден", userId);
                    return new NotFoundException("Пользователь с id " + userId + " не найден");
                });

        log.info("Проверка доступности предмета '{}': {}", item.getName(), item.getAvailable());
        if (!item.getAvailable()) {
            log.error("Ошибка валидации: предмет '{}' недоступен для бронирования", item.getName());
            throw new ValidationException(String.format("Айтем %s не может быть бронирован", item.getName()));
        }

        log.info("Проверка владельца предмета. Владелец item: {}, Пользователь: {}",
                item.getOwner() != null ? item.getOwner().getId() : "null", user.getId());

        if (item.getOwner() == null) {
            log.error("Ошибка: предмет с id {} не имеет владельца", item.getId());
            throw new ValidationException("Предмет не привязан к владельцу");
        }

        if (user.getId().equals(item.getOwner().getId())) {
            log.error("Ошибка: пользователь {} пытается забронировать свою же вещь", userId);
            throw new NotFoundException("Вы пытаетесь забронировать вещь, которая Вам принадлежит!");
        }

        log.info("Создание объекта бронирования...");
        Booking booking = BookingMapper.toBooking(bookingDto);

        log.info("Даты бронирования. Начало: {}, Окончание: {}", booking.getStart(), booking.getEnd());

        if (booking.getStart() == null || booking.getEnd() == null) {
            log.error("Ошибка: даты бронирования не заданы. Start: {}, End: {}", booking.getStart(), booking.getEnd());
            throw new ValidationException("Даты бронирования не могут быть null");
        }

        if (booking.getStart().isAfter(booking.getEnd()) || booking.getStart().isEqual(booking.getEnd())) {
            log.error("Ошибка валидации: Дата окончания брони неверна. Start: {}, End: {}",
                    booking.getStart(), booking.getEnd());
            throw new ValidationException("Дата окончания брони неверна");
        }

        booking.setItem(item);
        booking.setBooker(user);

        log.info("Сохранение бронирования в базе данных...");
        Booking savedBooking = bookingRepository.save(booking);

        log.info("Бронирование успешно сохранено с id: {}", savedBooking.getId());

        return BookingMapper.toBookingOutDto(savedBooking);
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
