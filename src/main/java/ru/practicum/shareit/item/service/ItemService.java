package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentOutputDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOutputDto;

import java.util.List;

public interface ItemService {
    ItemDto addItem(Integer userId, ItemDto itemDto);

    ItemDto editItem(Integer userId, Integer itemId, ItemDto itemDto);

    ItemDto getItem(Integer itemId);

    List<ItemDto> getItemsOfUser(Integer userId);

    List<ItemDto> search(String searchQuery);

    void deleteItem(Integer ownerId, Integer itemId);

    CommentOutputDto addComment(Integer userId, CommentDto commentDto, Integer itemId);

    ItemOutputDto addBookingInfoAndComments(Integer itemId, Integer userId);

}
