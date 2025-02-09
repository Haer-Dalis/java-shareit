package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repository.InMemoryItemRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final InMemoryItemRepository inMemoryItemRepository;

    @Override
    public ItemDto addItem(Integer itemId, ItemDto itemDto) {
        return inMemoryItemRepository.addItem(itemId, itemDto);
    }

    @Override
    public ItemDto editItem(Integer userId, Integer itemId, ItemDto itemDto) {
        return inMemoryItemRepository.editItem(userId, itemId, itemDto);
    }

    @Override
    public ItemDto getItem(Integer itemId) {
        return inMemoryItemRepository.getItem(itemId);
    }

    @Override
    public List<ItemDto> getItemsOfUser(Integer userId) {
        return inMemoryItemRepository.getItemsOfUser(userId);
    }

    @Override
    public List<ItemDto> search(String searchQuery) {
        return inMemoryItemRepository.search(searchQuery);
    }
}
