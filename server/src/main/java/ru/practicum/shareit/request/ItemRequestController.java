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
    public ItemRequestOutputDto addRequest(@RequestHeader(SHARER_ID_HEADER) Long userId,
                                           @RequestBody ItemRequestDto itemRequestDto) {
        try {
            log.info("Полученное DTO: {}", itemRequestDto);
            Integer userIdInt = Math.toIntExact(userId);
            return itemRequestService.addRequest(itemRequestDto, userIdInt);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка в addRequest (Server Controller): " + e.getMessage());
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
