package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {

    Booking addBooking(Integer userId, Integer itemId, Booking booking);

    Booking approveBooking(Integer ownerId, Integer bookingId, Boolean approved);

    Booking getBooking(Integer userId, Integer bookingId);

    List<Booking> getAllUserBookings(Integer userId, String state);

    List<Booking> getAllOwnerItemBookings(Integer userId, String state);

    Booking getById(Integer id);

}
