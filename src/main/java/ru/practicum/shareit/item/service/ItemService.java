package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto addItem(Integer userId, ItemDto itemDto);

    ItemDto editItem(Integer userId, Integer itemId, ItemDto itemDto);

    ItemDto getItem(Integer itemId);

    List<ItemDto> getItemsOfUser(Integer userId);

    List<ItemDto> search(String searchQuery);
}
