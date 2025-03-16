package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.HeaderConstants;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@Slf4j
@Validated
@Controller
@RequestMapping("/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@Valid @RequestBody ItemRequestDto itemRequestDto,
                                                    @RequestHeader(HeaderConstants.SHARER_ID_HEADER) long userId) {
        return itemRequestClient.addRequest(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getUserRequests(@RequestHeader(HeaderConstants.SHARER_ID_HEADER) Long userId) {
        log.info("Пользователь с id {} запрашивает свои бронирования", userId);
        return itemRequestClient.getRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader(HeaderConstants.SHARER_ID_HEADER) long userId) {
        log.info("Пользователь с id {} запрашивает список всех бронирований", userId);
        return itemRequestClient.getAllByUser(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequest(@RequestHeader(HeaderConstants.SHARER_ID_HEADER) Long userId,
                                             @Positive @PathVariable("requestId") Long requestId) {
        return itemRequestClient.getRequestById(userId, requestId);
    }

}
