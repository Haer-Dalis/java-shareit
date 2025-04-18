package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.ShareItApp;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemOutputDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
@ContextConfiguration(classes = ShareItApp.class)
public class BookingDtoTests {

    @Autowired
    private JacksonTester<BookingOutputDto> json;

    @Autowired
    private JacksonTester<BookingShortDto> jsonShort;

    private final LocalDateTime start = LocalDateTime.of(2025, 2, 2, 2, 2);
    private final LocalDateTime end = LocalDateTime.of(2025, 2, 3, 3, 3);

    @Test
    void testBookingOutDtoTesting() throws Exception {

        UserDto userDto = UserDto.builder()
                .id(1)
                .name("Alex")
                .email("alexFirst@yandex.ru")
                .build();

        ItemOutputDto testItemDto = ItemOutputDto.builder()
                .id(2)
                .name("itemName")
                .description("item description")
                .available(true)
                .requestId(1)
                .build();

        BookingOutputDto bookingOutDto = BookingOutputDto.builder()
                .id(3)
                .start(start)
                .end(end)
                .status(Status.APPROVED)
                .booker(userDto)
                .item(testItemDto)
                .build();

        JsonContent<BookingOutputDto> result = json.write(bookingOutDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(3);
        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo(start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo(end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("APPROVED");
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo("Alex");
        assertThat(result).extractingJsonPathStringValue("$.booker.email").isEqualTo(
                "alexFirst@yandex.ru");
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("itemName");
        assertThat(result).extractingJsonPathBooleanValue("$.item.available").isEqualTo(true);
    }

    @Test
    void testBookingShortDtoTesting() throws Exception {

        BookingShortDto bookingShortDto = BookingShortDto.builder()
                .id(4)
                .bookerId(1)
                .start(start)
                .end(end)
                .build();

        JsonContent<BookingShortDto> result = jsonShort.write(bookingShortDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(4);
        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo(start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo(end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        assertThat(result).extractingJsonPathNumberValue("$.bookerId")
                .isEqualTo(1);
    }

}