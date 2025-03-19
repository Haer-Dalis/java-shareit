package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.HeaderConstants;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestOutputDto;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ItemRequestOutputDto createItemRequest(@Valid @RequestBody ItemRequestDto itemRequestDto,
                                                  @RequestHeader(HeaderConstants.SHARER_ID_HEADER) long userId) {
        log.info("Создание запроса в Controller Gateway: userId={}, requestDto={}", userId, itemRequestDto);
        ItemRequestOutputDto response = itemRequestClient.addRequest(userId, itemRequestDto);
        log.info("Ответ от клиента в Controller Gateway: {}", response);
        return response;
    }

    @GetMapping
    public List<ItemRequestOutputDto> getAll(@RequestHeader(HeaderConstants.SHARER_ID_HEADER) long userId) {
        return itemRequestClient.getAllRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestOutputDto> getAllFromUser(@RequestHeader(HeaderConstants.SHARER_ID_HEADER) long userId) {
        return itemRequestClient.getAllByUser(userId);
    }

    @GetMapping("/{request-id}")
    public ItemRequestOutputDto getItemRequest(@RequestHeader(HeaderConstants.SHARER_ID_HEADER) long userId,
                                               @PathVariable("request-id") long requestId) {
        return itemRequestClient.getItemRequest(userId, requestId);
    }
}
