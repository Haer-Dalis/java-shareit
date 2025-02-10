package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.InMemoryItemRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final InMemoryItemRepository inMemoryItemRepository;
    private final ItemMapper itemMapper;

    @Override
    public ItemDto addItem(Integer userId, ItemDto itemDto) {
        Item item = itemMapper.toItem(itemDto);
        Item savedItem = inMemoryItemRepository.addItem(userId, item);
        return itemMapper.toItemDto(savedItem);
    }

    @Override
    public ItemDto editItem(Integer userId, Integer itemId, ItemDto itemDto) {
        Item item = inMemoryItemRepository.getItem(itemId);
        if (!item.getOwner().getId().equals(userId)) {
            throw new AccessDeniedException("Попытка несанкционированного доступа");
        }
        itemMapper.updateItemFromDto(item, itemDto);
        return itemMapper.toItemDto(item);
    }

    @Override
    public ItemDto getItem(Integer itemId) {
        Item item = inMemoryItemRepository.getItem(itemId);
        return itemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getItemsOfUser(Integer userId) {
        return inMemoryItemRepository.getItemsOfUser(userId).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String searchQuery) {
        return inMemoryItemRepository.search(searchQuery).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
