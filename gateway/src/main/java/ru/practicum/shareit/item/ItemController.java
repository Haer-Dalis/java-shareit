package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.HeaderConstants;
import ru.practicum.shareit.item.dto.*;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getOwnerItems(@RequestHeader(HeaderConstants.SHARER_ID_HEADER) Integer userId) {
        log.info("Get owner items");
        return itemClient.getItems(userId);
    }

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader(HeaderConstants.SHARER_ID_HEADER) Integer userId, @Valid @RequestBody ItemDto item) {
        return itemClient.addItem(item, userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@PathVariable Integer itemId) {
        log.info("Get item {}", itemId);
        return itemClient.findItemById(itemId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@PathVariable Integer itemId, @RequestBody ItemDtoUpdate item,
                                             @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemClient.updateItem(item, itemId, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestHeader(HeaderConstants.SHARER_ID_HEADER) long userId,
                                              @NotNull @RequestParam String text) {
        log.info("Запущен поиск по тексту: {}", text);
        return itemClient.searchItems(userId, text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@PathVariable Integer itemId,
                                             @RequestHeader("X-Sharer-User-Id") Integer userId,
                                             @RequestBody @Valid CommentDto text) {
        return itemClient.addComment(itemId, userId, text);
    }

}
