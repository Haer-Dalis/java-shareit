package ru.practicum.shareit.item.repository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.repository.InMemoryUserRepository;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
@AllArgsConstructor
public class InMemoryItemRepository {
    private final InMemoryUserRepository inMemoryUserRepository;
    private final Map<Integer, Item> items = new HashMap<>();

    public Item addItem(Integer userId, Item item) {
        if (inMemoryUserRepository.getUser(userId) != null) {
            int id = generateId();
            item.setId(id);
            item.setOwner(inMemoryUserRepository.getUser(userId));
            items.put(id, item);
            log.info("Успешно добавлен предмет с id {}", id);
            return item;
        } else {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }
    }

    public Item getItem(Integer itemId) {
        if (items.containsKey(itemId)) {
            log.info("Найден предмет под номером {}", itemId);
            return items.get(itemId);
        } else {
            throw new NotFoundException("Предмет с id " + itemId + " не найден");
        }
    }

    public List<Item> getItemsOfUser(Integer userId) {
        return items.values().stream()
                .filter(item -> item.getOwner() != null && item.getOwner().getId().equals(userId))
                .collect(Collectors.toList());
    }

    public List<Item> search(String searchQuery) {
        if (searchQuery == null || searchQuery.isBlank()) {
            return Collections.emptyList();
        }
        String lowerCaseQuery = searchQuery.toLowerCase();
        return items.values().stream()
                .filter(item -> item.getAvailable() != null && item.getAvailable()
                        && (item.getName().toLowerCase().contains(lowerCaseQuery)
                        || item.getDescription().toLowerCase().contains(lowerCaseQuery)))
                .collect(Collectors.toList());
    }

    private Integer generateId() {
        if (items.isEmpty()) {
            return 1;
        } else {
            return Collections.max(items.keySet()) + 1;
        }
    }

}
