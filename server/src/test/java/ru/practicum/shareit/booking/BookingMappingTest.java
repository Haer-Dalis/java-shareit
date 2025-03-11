package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.assertThat;

class BookingMappingTest {

    @Test
    void toBookingOutDto_shouldMapCorrectly() {
        User booker = User.builder()
                .name("Ivan")
                .email("ivanovemail@yandex.ru")
                .build();
        Item item = Item.builder()
                .name("Item1")
                .description("Description1")
                .available(true)
                .build();
        Booking booking = new Booking(10, LocalDateTime.now(), LocalDateTime.now().plusDays(1), item, booker, Status.APPROVED);

        BookingOutputDto dto = BookingMapper.toBookingOutDto(booking);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(booking.getId());
        assertThat(dto.getStart()).isEqualTo(booking.getStart());
        assertThat(dto.getEnd()).isEqualTo(booking.getEnd());
        assertThat(dto.getStatus()).isEqualTo(booking.getStatus());
        assertThat(dto.getBooker()).isNotNull();
        assertThat(dto.getItem()).isNotNull();
    }

    @Test
    void toBookingShortDto_shouldMapCorrectly() {
        User booker = User.builder()
                .name("Ivan")
                .email("ivanovemail@yandex.ru")
                .build();
        Booking booking = new Booking(10, LocalDateTime.now(), LocalDateTime.now().plusDays(1), new Item(), booker, Status.APPROVED);

        BookingShortDto dto = BookingMapper.toBookingShortDto(booking);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(booking.getId());
        assertThat(dto.getBookerId()).isEqualTo(booking.getBooker().getId());
        assertThat(dto.getStart()).isEqualTo(booking.getStart());
        assertThat(dto.getEnd()).isEqualTo(booking.getEnd());
    }

    @Test
    void toBooking_shouldMapCorrectly() {
        BookingDto bookingDto = new BookingDto(1, LocalDateTime.now(), LocalDateTime.now().plusDays(1), Status.WAITING);

        Booking booking = BookingMapper.toBooking(bookingDto);

        assertThat(booking).isNotNull();
        assertThat(booking.getStart()).isEqualTo(bookingDto.getStart());
        assertThat(booking.getEnd()).isEqualTo(bookingDto.getEnd());
        assertThat(booking.getStatus()).isEqualTo(Status.WAITING);
    }
}

