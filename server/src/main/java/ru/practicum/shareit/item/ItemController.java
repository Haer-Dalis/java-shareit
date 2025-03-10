package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto addNewItem(@RequestHeader(HeaderConstants.SHARER_ID_HEADER) Integer userId,
                              @Valid @RequestBody ItemDto itemDto) {
        return itemService.addItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto editItem(@RequestHeader(HeaderConstants.SHARER_ID_HEADER) Integer userId,
                            @PathVariable Integer itemId,
                            @RequestBody ItemDto itemDto) {
        return itemService.editItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemOutputDto getItem(
            @RequestHeader(HeaderConstants.SHARER_ID_HEADER) Integer userId,
            @Positive @PathVariable Integer itemId) {
        return itemService.addBookingInfoAndComments(itemId, userId);
    }

    @GetMapping
    public List<ItemDto> getItemsOfUser(@RequestHeader(HeaderConstants.SHARER_ID_HEADER) Integer userId) {
        return itemService.getItemsOfUser(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam(name = "text") String searchQuery) {
        return itemService.search(searchQuery);

    }

    @PostMapping("/{itemId}/comment")
    public CommentOutputDto addComment(@RequestHeader(HeaderConstants.SHARER_ID_HEADER) Integer userId,
                                       @Valid @RequestBody CommentDto commentDto,
                                       @Positive @PathVariable Integer itemId) {
        return itemService.addComment(userId, commentDto, itemId);
    }

}
