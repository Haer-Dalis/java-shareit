package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.client.BaseClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BookingClient extends BaseClient {

    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl,
                         RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public BookingOutputDto getBooking(Long userId, Long bookingId) {
        ResponseEntity<Object> response = get("/" + bookingId, userId);
        return (BookingOutputDto) response.getBody();
    }

    public List<BookingOutputDto> getBookings(Long userId, String state) {
        ResponseEntity<Object> response = get("?state=" + state, userId);
        return (List<BookingOutputDto>) response.getBody();
    }

    public BookingOutputDto createBooking(long userId, CreateBookingDto requestDto) {
        ResponseEntity<Object> response = post("", userId, requestDto);
        return (BookingOutputDto) response.getBody();
    }

    public BookingOutputDto processBooking(Long userId, Long bookingId, Boolean approved) {
        Map<String, Object> params = new HashMap<>();
        params.put("approved", approved);
        ResponseEntity<Object> response = patch("/" + bookingId + "?approved={approved}", userId, params, null);
        return (BookingOutputDto) response.getBody();
    }

    public List<BookingOutputDto> findByOwner(long ownerId, BookingState state) {
        ResponseEntity<Object> response = get("/owner?state=" + state.name(), ownerId);
        return (List<BookingOutputDto>) response.getBody();
    }
}