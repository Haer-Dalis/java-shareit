package ru.practicum.shareit.request;

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
    public ResponseEntity<Object> addRequest(@RequestHeader(HeaderConstants.SHARER_ID_HEADER) Long userId,
                                             @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Пользователь с id {} добавил запрос бронирования", userId);

        if (userId == null) {
            throw new IllegalArgumentException("userId не должен быть null");
        }
        if (itemRequestDto == null) {
            throw new IllegalArgumentException("itemRequestDto не должен быть null");
        }

        try {
            return itemRequestClient.addRequest(userId, itemRequestDto);
        } catch (Exception e) {
            throw new RuntimeException(
                    String.format("Ошибка в addRequest (контроллер). userId=%d, DTO=%s", userId, itemRequestDto), e);
        }
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
