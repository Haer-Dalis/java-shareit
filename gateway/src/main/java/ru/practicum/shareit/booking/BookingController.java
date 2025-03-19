package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.HeaderConstants;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.dto.CreateBookingDto;

import java.util.List;

@Validated
@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingClient bookingClient;

    @GetMapping
    public List<BookingOutputDto> getBookings(
            @RequestHeader(HeaderConstants.SHARER_ID_HEADER) long userId,
            @RequestParam(name = "state", defaultValue = "ALL") String stateParam) {
        return bookingClient.getBookings(userId, stateParam).getBody();
    }

    @PostMapping
    public BookingOutputDto createBooking(
            @RequestHeader(HeaderConstants.SHARER_ID_HEADER) long userId,
            @RequestBody @Valid CreateBookingDto requestDto) {
        return bookingClient.createBooking(userId, requestDto).getBody();
    }

    @PatchMapping("/{bookingId}")
    public BookingOutputDto processBooking(
            @RequestHeader(HeaderConstants.SHARER_ID_HEADER) Long userId,
            @PathVariable Long bookingId,
            @RequestParam Boolean approved) {
        return bookingClient.processBooking(userId, bookingId, approved).getBody();
    }

    @GetMapping("/{bookingId}")
    public BookingOutputDto getBooking(
            @RequestHeader(HeaderConstants.SHARER_ID_HEADER) long userId,
            @PathVariable Long bookingId) {
        return bookingClient.getBooking(userId, bookingId).getBody();
    }

    @GetMapping("/owner")
    public List<BookingOutputDto> findByOwner(
            @RequestHeader(HeaderConstants.SHARER_ID_HEADER) Long ownerId,
            @RequestParam(defaultValue = "ALL") BookingState state) {
        return bookingClient.findByOwner(ownerId, state).getBody();
    }
}