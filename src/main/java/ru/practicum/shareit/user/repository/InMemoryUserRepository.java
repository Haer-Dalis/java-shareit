package ru.practicum.shareit.user.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.User;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Repository
public class InMemoryUserRepository {
    private final Map<Integer, User> users = new HashMap<>();
    private final Set<String> allEmails = new HashSet<>();

    public User addUser(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ValidationException("У пользователя отсутствует почта!");
        }
        if (allEmails.contains(user.getEmail())) {
            throw new ConflictException("Такая почта уже существует: " + user.getEmail());
        }
        allEmails.add(user.getEmail());
        Integer id = generateId();
        user.setId(id);
        users.put(id, user);
        log.info("Добавлен пользователь с {}", id);
        return user;
    }

    public User updateUser(User updatedUser, Integer userId) {
        User user = users.get(userId);
        if (user == null) {
            log.error("Пользователь с id {} не найден.", userId);
            throw new NotFoundException("Пользователь с id " + userId + " не найден.");
        }

        if (updatedUser.getEmail() != null && !updatedUser.getEmail().equals(user.getEmail())) {
            if (allEmails.contains(updatedUser.getEmail())) {
                throw new ConflictException("Такая почта уже существует: " + updatedUser.getEmail());
            }
            allEmails.add(updatedUser.getEmail());
            allEmails.remove(user.getEmail());
            user.setEmail(updatedUser.getEmail());
        }

        if (updatedUser.getName() != null) {
            user.setName(updatedUser.getName());
        }
        log.info("Пользователь с id {} успешно обновлен", userId);
        return user;

    }

    public User deleteUser(Integer userId) {
        User user = users.remove(userId);

        if (user == null) {
            log.warn("Пользователь с id {} не найден", userId);
            throw new NotFoundException("Пользователь с id " + userId + " не найден.");
        }

        allEmails.remove(user.getEmail());

        log.info("Удален пользователь с id {} ", userId);
        return user;
    }

    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    public User getUser(Integer userId) {
        return users.get(userId);
    }

    private Integer generateId() {
        if (users.isEmpty()) {
            return 1;
        } else {
            return Collections.max(users.keySet()) + 1;
        }
    }

}
