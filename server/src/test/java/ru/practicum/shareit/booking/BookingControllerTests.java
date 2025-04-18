package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemOutputDto;

import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class BookingControllerTests {

    @Autowired
    private MockMvc mvc;

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController bookingController;

    private ObjectMapper objectMapper;

    private BookingDto createDto;
    private BookingOutputDto resultDto;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.findAndRegisterModules();

        mvc = MockMvcBuilders.standaloneSetup(bookingController).build();

        createDto = BookingDto.builder()
                .start(LocalDateTime.of(2025, 5, 1, 12, 0))
                .end(LocalDateTime.of(2025, 5, 10, 12, 0))
                .itemId(1)
                .build();

        resultDto = BookingOutputDto.builder()
                .id(1)
                .booker(UserDto.builder().id(2).build())
                .start(LocalDateTime.of(2025, 5, 1, 12, 0))
                .end(LocalDateTime.of(2025, 5, 10, 12, 0))
                .item(ItemOutputDto.builder().id(1).name("Test Item").build())
                .status(Status.APPROVED)
                .build();
    }

    @Test
    void addBookingTesting() throws Exception {
        when(bookingService.addBooking(eq(2), any(BookingDto.class)))
                .thenReturn(resultDto);

        String requestBody = objectMapper.writeValueAsString(createDto);
        System.out.println("Request body: " + requestBody);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 2)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(resultDto.getId()))
                .andExpect(jsonPath("$.booker.id").value(resultDto.getBooker().getId()))
                .andExpect(jsonPath("$.status").value(resultDto.getStatus().name()))
                .andExpect(jsonPath("$.item.id").value(resultDto.getItem().getId()))
                .andExpect(jsonPath("$.item.name").value(resultDto.getItem().getName()));
    }

    @Test
    void approveBookingTesting() throws Exception {
        when(bookingService.approveBooking(eq(2), eq(1), eq(true)))
                .thenReturn(resultDto);

        mvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 2)
                        .param("approved", "true")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(resultDto.getId()))
                .andExpect(jsonPath("$.status").value(resultDto.getStatus().name()));
    }

    @Test
    void getBookingTesting() throws Exception {
        when(bookingService.getBooking(eq(2), eq(1)))
                .thenReturn(resultDto);

        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 2)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(resultDto.getId()))
                .andExpect(jsonPath("$.booker.id").value(resultDto.getBooker().getId()))
                .andExpect(jsonPath("$.status").value(resultDto.getStatus().name()))
                .andExpect(jsonPath("$.item.id").value(resultDto.getItem().getId()))
                .andExpect(jsonPath("$.item.name").value(resultDto.getItem().getName()));
    }

    @Test
    void getUsersBookingsTesting() throws Exception {
        when(bookingService.getAllUserBookings(eq(2), eq("ALL")))
                .thenReturn(Collections.singletonList(resultDto));

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 2)
                        .param("state", "ALL")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(resultDto.getId()))
                .andExpect(jsonPath("$[0].booker.id").value(resultDto.getBooker().getId()))
                .andExpect(jsonPath("$[0].start[0]").value(2025))
                .andExpect(jsonPath("$[0].end[0]").value(2025))
                .andExpect(jsonPath("$[0].status").value(resultDto.getStatus().name()))
                .andExpect(jsonPath("$[0].item.id").value(resultDto.getItem().getId()))
                .andExpect(jsonPath("$[0].item.name").value(resultDto.getItem().getName()));
    }

    @Test
    void getAllItemBookingsOfOwnerTesting() throws Exception {
        when(bookingService.getAllOwnerItemBookings(eq(2), eq("ALL")))
                .thenReturn(Collections.singletonList(resultDto));

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 2)
                        .param("state", "ALL")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(resultDto.getId()))
                .andExpect(jsonPath("$[0].booker.id").value(resultDto.getBooker().getId()))
                .andExpect(jsonPath("$[0].start[0]").value(2025))
                .andExpect(jsonPath("$[0].end[0]").value(2025))
                .andExpect(jsonPath("$[0].status").value(resultDto.getStatus().name()))
                .andExpect(jsonPath("$[0].item.id").value(resultDto.getItem().getId()))
                .andExpect(jsonPath("$[0].item.name").value(resultDto.getItem().getName()));
    }
}