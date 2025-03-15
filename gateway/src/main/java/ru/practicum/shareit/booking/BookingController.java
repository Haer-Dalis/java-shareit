package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.dto.CreateBookingDto;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private static final String HEADER_USER_PARAMETER = "X-Sharer-User-Id";

    private final BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> getBookings(@RequestHeader(HEADER_USER_PARAMETER) long userId,
                                             @RequestParam(name = "state", defaultValue = "ALL") String stateParam) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        return bookingClient.getBookings(userId, state);
    }

    @PostMapping
    public ResponseEntity<Object> createBooking(
            @RequestHeader(HEADER_USER_PARAMETER) long userId,
            @RequestBody @Valid CreateBookingDto requestDto) {
        return bookingClient.createBooking(userId, requestDto);
    }

    @PatchMapping("/{bookingId}")
    ResponseEntity<Object> processBooking(
            @RequestHeader(HEADER_USER_PARAMETER) Long userId,
            @PathVariable Long bookingId,
            @RequestParam Boolean approved) {
        return bookingClient.processBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(
            @RequestHeader(HEADER_USER_PARAMETER) long userId,
            @PathVariable Long bookingId) {
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping("/owner")
    ResponseEntity<Object> findByOwner(
            @RequestHeader(HEADER_USER_PARAMETER) Long ownerId,
            @RequestParam(defaultValue = "ALL") BookingState state) {
        return bookingClient.findByOwner(ownerId, state);
    }
}