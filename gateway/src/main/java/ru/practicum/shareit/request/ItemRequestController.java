package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.HeaderConstants;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@Valid @RequestBody ItemRequestDto itemRequestDto,
                                                    @RequestHeader(HeaderConstants.SHARER_ID_HEADER) long userId) {
        log.info("Создание запроса в Controller Gateway: userId={}, requestDto={}", userId, itemRequestDto);
        ResponseEntity<Object> response = itemRequestClient.addRequest(userId, itemRequestDto);
        log.info("Ответ от клиента в Controller Gateway: {}", response);
        return response;
    }

    @GetMapping
    public ResponseEntity<Object> getAll(@RequestHeader(HeaderConstants.SHARER_ID_HEADER) long userId) {
        return itemRequestClient.getAllRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllFromUser(@RequestHeader(HeaderConstants.SHARER_ID_HEADER) long userId) {
        return itemRequestClient.getAllByUser(userId);
    }

    @GetMapping("/{request-id}")
    public ResponseEntity<Object> getItemRequest(@RequestHeader(HeaderConstants.SHARER_ID_HEADER) long userId,
                                                 @PathVariable("request-id") long requestId) {
        return itemRequestClient.getItemRequest(userId, requestId);
    }
}
