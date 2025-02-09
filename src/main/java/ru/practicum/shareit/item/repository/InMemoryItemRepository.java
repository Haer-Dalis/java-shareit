package ru.practicum.shareit.item.repository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.repository.InMemoryUserRepository;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
@AllArgsConstructor
public class InMemoryItemRepository {
    private final InMemoryUserRepository inMemoryUserRepository;
    private final ItemMapper itemMapper;
    private Map<Integer, Item> items = new HashMap<>();

    public ItemDto addItem(Integer userId, ItemDto itemDto) {
        if (inMemoryUserRepository.getUser(userId) != null) {
            Item item = itemMapper.toItem(itemDto);
            int id = generateId();
            item.setId(id);
            item.setOwner(inMemoryUserRepository.getUser(userId));
            items.put(id, item);
            log.info("Успешно добавлен предмет с id {}", id);
            return itemMapper.toItemDto(item);
        } else {
            throw new NotFoundException("Пользователь с id " + userId + "не найден");
        }
    }

    public ItemDto editItem(Integer userId, Integer itemId, ItemDto itemDto) {
        if (items.get(itemId) != null) {
            if (Objects.equals(items.get(itemId).getOwner().getId(), userId)) {
                Item item = items.get(itemId);
                item.setName(itemDto.getName());
                item.setDescription(itemDto.getDescription());
                item.setAvailable(itemDto.getAvailable());
                items.put(itemId, item);
                log.info("Предмет обновлен. Его id {}", itemId);
                return itemMapper.toItemDto(item);
            } else {
                throw new AccessDeniedException("Попытка несанкционированного доступа со стороны user id " + userId);
            }
        } else {
            throw new NotFoundException("Вещи с id " + itemId + " нет.");
        }

    }

    public ItemDto getItem(Integer itemId) {
        if (items.containsKey(itemId)) {
            log.info("Найден предмет под номером {}", itemId);
            return itemMapper.toItemDto(items.get(itemId));
        } else {
            throw new NotFoundException("Предмет с id " + itemId + " не отыскался");
        }
    }

    public List<ItemDto> getItemsOfUser(Integer userId) {
        List<ItemDto> itemsOfUser = items.values().stream()
                .filter(item -> item.getOwner() != null && item.getOwner().getId().equals(userId))
                .map(item -> itemMapper.toItemDto(item))
                .collect(Collectors.toList());

        if (itemsOfUser.isEmpty()) {
            throw new NotFoundException("Вещи пользователя " + userId + " не отыскались");
        }

        return itemsOfUser;
    }

    public List<ItemDto> search(String searchQuery) {
        if (searchQuery == null || searchQuery.isBlank()) {
            return Collections.emptyList();
        }
        String lowerCaseQuery = searchQuery.toLowerCase();
        List<ItemDto> itemsFound = new ArrayList<>();
        for (Item item : items.values()) {
            if (item == null || item.getAvailable() == null || !item.getAvailable()) {
                continue;
            }
            String name = item.getName() != null ? item.getName().toLowerCase() : "";
            String description = item.getDescription() != null ? item.getDescription().toLowerCase() : "";

            if (name.contains(lowerCaseQuery) || description.contains(lowerCaseQuery)) {
                itemsFound.add(itemMapper.toItemDto(item));
            }
        }
        log.info("Поиск: '{}', найдено {} вещей", searchQuery, itemsFound.size());
        return itemsFound;
    }

    private Integer generateId() {
        if (items.isEmpty()) {
            return 1;
        } else {
            return Collections.max(items.keySet()) + 1;
        }
    }

}
