package ru.practicum.shareit.request;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestOutputDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

import static ru.practicum.shareit.item.HeaderConstants.SHARER_ID_HEADER;

@Slf4j
@Validated
@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestOutputDto addRequest(@RequestHeader(SHARER_ID_HEADER) Integer userId,
                                           @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Полученное DTO: {}", itemRequestDto);

        if (userId == null) {
            throw new IllegalArgumentException("userId не должен быть null (service)");
        }
        if (itemRequestDto == null) {
            throw new IllegalArgumentException("itemRequestDto не должен быть null (service)");
        }

        try {
            return itemRequestService.addRequest(itemRequestDto, userId);
        } catch (Exception e) {
            log.error("Ошибка в addRequest (service). userId: {}, DTO: {}", userId, itemRequestDto, e);
            throw new RuntimeException("Ошибка в сервисе при добавлении запроса", e);
        }
    }

    @GetMapping
    public List<ItemRequestOutputDto> getUserRequests(@RequestHeader(SHARER_ID_HEADER) Integer userId) {
        return itemRequestService.getUserRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestOutputDto> getAllRequestsExceptOneUser(@RequestHeader(SHARER_ID_HEADER) Integer userId) {
        return itemRequestService.getAllRequestsExceptOneUser(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestOutputDto getRequest(@RequestHeader(SHARER_ID_HEADER) Integer userId,
                                           @Positive @PathVariable("requestId") Integer requestId) {
        return itemRequestService.getRequest(userId, requestId);
    }
}
