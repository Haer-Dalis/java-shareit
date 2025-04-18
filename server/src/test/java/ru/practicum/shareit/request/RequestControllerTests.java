package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestOutputDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RequestControllerTests {

    @Mock
    private ItemRequestService itemRequestService;
    @InjectMocks
    private ItemRequestController itemRequestController;
    private MockMvc mvc;
    private ObjectMapper objectMapper;
    private ItemRequestDto createDto;
    private ItemRequestOutputDto outputDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mvc = MockMvcBuilders.standaloneSetup(itemRequestController).build();

        createDto = ItemRequestDto.builder()
                .description("Description")
                .build();

        outputDto = ItemRequestOutputDto.builder()
                .id(1)
                .description("Description")
                .created(LocalDateTime.of(2025, 3, 10, 10, 10, 10))
                .items(Collections.emptyList())
                .build();
    }

    @Test
    void addRequestTesting() throws Exception {
        when(itemRequestService.addRequest(any(ItemRequestDto.class), eq(1))).thenReturn(outputDto);
        System.out.println("JSON: " + objectMapper.writeValueAsString(createDto));

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(createDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(outputDto.getId()))
                .andExpect(jsonPath("$.description").value(outputDto.getDescription()))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items").isEmpty())
                .andExpect(jsonPath("$.created").value("2025-03-10T10:10:10"));

        verify(itemRequestService, times(1)).addRequest(any(ItemRequestDto.class), eq(1));
    }

    @Test
    void getUserRequestsTesting() throws Exception {
        List<ItemRequestOutputDto> requests = List.of(outputDto);
        when(itemRequestService.getUserRequests(1)).thenReturn(requests);

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id").value(outputDto.getId()))
                .andExpect(jsonPath("$[0].description").value(outputDto.getDescription()))
                .andExpect(jsonPath("$[0].items").isArray())
                .andExpect(jsonPath("$[0].items").isEmpty())
                .andExpect(jsonPath("$[0].created").value("2025-03-10T10:10:10"));

        verify(itemRequestService, times(1)).getUserRequests(1);
    }

    @Test
    void getAllRequestsExceptOneUserTesting() throws Exception {
        List<ItemRequestOutputDto> requests = List.of(outputDto);
        when(itemRequestService.getAllRequestsExceptOneUser(1)).thenReturn(requests);

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id").value(outputDto.getId()))
                .andExpect(jsonPath("$[0].description").value(outputDto.getDescription()))
                .andExpect(jsonPath("$[0].items").isArray())
                .andExpect(jsonPath("$[0].items").isEmpty())
                .andExpect(jsonPath("$[0].created").value("2025-03-10T10:10:10"));

        verify(itemRequestService, times(1)).getAllRequestsExceptOneUser(1);
    }

    @Test
    void getRequestTesting() throws Exception {
        when(itemRequestService.getRequest(2, 1)).thenReturn(outputDto);

        mvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 2)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(outputDto.getId()))
                .andExpect(jsonPath("$.description").value(outputDto.getDescription()))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items").isEmpty());

        verify(itemRequestService, times(1)).getRequest(2, 1);
    }
}