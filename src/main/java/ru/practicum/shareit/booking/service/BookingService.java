package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {

    BookingOutputDto addBooking(Integer userId, BookingDto bookingDto);

    BookingOutputDto approveBooking(Integer ownerId, Integer bookingId, Boolean approved);

    BookingOutputDto getBooking(Integer userId, Integer bookingId);

    List<BookingOutputDto> getAllUserBookings(Integer userId, String state);

    List<BookingOutputDto> getAllOwnerItemBookings(Integer userId, String state);

    Booking getById(Integer id);

}
