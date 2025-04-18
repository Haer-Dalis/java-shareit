package ru.practicum.shareit.booking;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.HeaderConstants;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingOutputDto addBooking(@RequestHeader(HeaderConstants.SHARER_ID_HEADER) Integer userId,
                                       @RequestBody BookingDto bookingDto) {
        return bookingService.addBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingOutputDto approveBooking(@RequestHeader(HeaderConstants.SHARER_ID_HEADER) Integer ownerId,
                                           @Positive @PathVariable Integer bookingId,
                                           @RequestParam Boolean approved) {
        return bookingService.approveBooking(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingOutputDto getBooking(@RequestHeader(HeaderConstants.SHARER_ID_HEADER) Integer userId,
                                       @Positive @PathVariable Integer bookingId) {
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping
    public List<BookingOutputDto> getUsersBookings(@RequestHeader(HeaderConstants.SHARER_ID_HEADER) Integer userId,
                                                   @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getAllUserBookings(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingOutputDto> getAllItemBookingsOfOwner(@RequestHeader(HeaderConstants.SHARER_ID_HEADER) Integer userId,
                                                            @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getAllOwnerItemBookings(userId, state);
    }
}
