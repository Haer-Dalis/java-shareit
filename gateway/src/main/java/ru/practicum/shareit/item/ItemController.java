package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.HeaderConstants;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentOutputDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOutputDto;

import java.util.List;

@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ItemDto createItem(@RequestHeader(HeaderConstants.SHARER_ID_HEADER) long userId,
                              @Valid @RequestBody ItemDto itemDto) {
        return itemClient.addItem(userId, itemDto);
    }

    @PatchMapping("/{item-id}")
    public ItemDto updateItem(@RequestHeader(HeaderConstants.SHARER_ID_HEADER) Long userId,
                              @PathVariable("item-id") Long itemId,
                              @RequestBody ItemDto itemUpdateDto) {
        return itemClient.updateItem(userId, itemId, itemUpdateDto);
    }

    @GetMapping("/{item-id}")
    public ItemOutputDto getItem(@RequestHeader(HeaderConstants.SHARER_ID_HEADER) long userId,
                                 @PathVariable("item-id") long itemId) {
        return itemClient.getItem(userId, itemId);
    }

    @GetMapping
    public List<ItemDto> getAllItems(@RequestHeader(HeaderConstants.SHARER_ID_HEADER) long userId) {
        return itemClient.getAllItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@NotNull @RequestParam String text) {
        return itemClient.searchItems(text);
    }

    @PostMapping("/{item-id}/comment")
    public CommentOutputDto createComment(@RequestHeader(HeaderConstants.SHARER_ID_HEADER) Long userId,
                                          @PathVariable("item-id") Long itemId,
                                          @Valid @RequestBody CommentDto commentDto) {
        return itemClient.createComment(userId, itemId, commentDto);
    }
}

