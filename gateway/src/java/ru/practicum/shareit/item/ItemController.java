package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
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
        log.info("Пользователь {} добавляет предмет: {}", userId, itemDto.getId());
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
    public ResponseEntity<Object> getAllOwnerItems(
            @RequestHeader(HeaderConstants.SHARER_ID_HEADER) Long userId,
            @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
            @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Пользователь {} запросил список своих вещей", userId);
        return itemClient.getItemsUser(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(
            @RequestParam(defaultValue = "") String text,
            @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
            @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Запущен поиск по тексту: {}", text);
        return itemClient.searchItem(text, from, size);
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
