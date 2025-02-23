package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.HeaderConstants;

import java.util.List;
import java.util.stream.Collectors;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingOutputDto addBooking(@RequestHeader(HeaderConstants.SHARER_ID_HEADER) Integer userId,
                                    @Valid @RequestBody BookingDto bookingDto) {
        if (bookingDto.getItemId() == null) {
            throw new ValidationException("Ошибка: itemId не может быть null");
        }
        Booking booking = bookingService.addBooking(
                userId, bookingDto.getItemId(), BookingMapper.toBooking(bookingDto));
        return BookingMapper.toBookingOutDto(booking);
    }

    @PatchMapping("/{bookingId}")
    public BookingOutputDto approveBooking(@RequestHeader(HeaderConstants.SHARER_ID_HEADER) Integer ownerId,
                                        @Positive @PathVariable Integer bookingId,
                                        @RequestParam Boolean approved) {
        Booking booking = bookingService.approveBooking(ownerId, bookingId, approved);
        return BookingMapper.toBookingOutDto(booking);
    }

    @GetMapping("/{bookingId}")
    public BookingOutputDto getBooking(@RequestHeader(HeaderConstants.SHARER_ID_HEADER) Integer userId,
                                    @Positive @PathVariable Integer bookingId) {
        Booking booking = bookingService.getBooking(userId, bookingId);
        return BookingMapper.toBookingOutDto(booking);
    }

    @GetMapping
    public List<BookingOutputDto> getUsersBookings(@RequestHeader(HeaderConstants.SHARER_ID_HEADER) Integer userId,
                                                  @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getAllUserBookings(userId, state).stream()
                .map(BookingMapper::toBookingOutDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/owner")
    public List<BookingOutputDto> getAllItemBookingsOfOwner(@RequestHeader(HeaderConstants.SHARER_ID_HEADER) Integer userId,
                                                          @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getAllOwnerItemBookings(userId, state).stream()
                .map(BookingMapper::toBookingOutDto)
                .collect(Collectors.toList());
    }
}
