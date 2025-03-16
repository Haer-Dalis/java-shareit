package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.HeaderConstants;
import ru.practicum.shareit.item.dto.*;

@Slf4j
@Validated
@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> addItem(
            @RequestHeader(HeaderConstants.SHARER_ID_HEADER) Long userId,
            @Valid @RequestBody ItemDto itemDto) {
        return itemClient.addItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(
            @RequestHeader(HeaderConstants.SHARER_ID_HEADER) Long userId,
            @RequestBody ItemDto itemDto,
            @Positive @PathVariable Long itemId) {
        log.info("Владелец {} обновил предмет: {}", userId, itemId);
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(
            @RequestHeader(HeaderConstants.SHARER_ID_HEADER) Long userId,
            @Positive @PathVariable Long itemId) {
        log.info("Пользователь {} запросил предмет: {}", userId, itemId);
        return itemClient.getItemById(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItems(@RequestHeader(HeaderConstants.SHARER_ID_HEADER) long userId) {
        return itemClient.getAllItems(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestHeader(HeaderConstants.SHARER_ID_HEADER) long userId,
                                              @NotNull @RequestParam String text) {
        log.info("Запущен поиск по тексту: {}", text);
        return itemClient.searchItems(userId, text);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> deleteItem(
            @RequestHeader(HeaderConstants.SHARER_ID_HEADER) Long userId,
            @Positive @PathVariable Long itemId) {
        log.info("Владелец {} удаляет предмет: {}", userId, itemId);
        return itemClient.deleteItem(userId, itemId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(
            @RequestHeader(HeaderConstants.SHARER_ID_HEADER) Long userId,
            @Valid @RequestBody CommentDto commentDto,
            @Positive @PathVariable Long itemId) {
        log.info("Пользователь с id: {} комментирует вещь с id: {}", userId, itemId);
        return itemClient.addComment(userId, itemId, commentDto);
    }

}

