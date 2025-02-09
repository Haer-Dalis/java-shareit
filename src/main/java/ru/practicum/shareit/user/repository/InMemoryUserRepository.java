package ru.practicum.shareit.user.repository;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Repository
public class InMemoryUserRepository {
    private final UserMapper userMapper;
    private Map<Integer, User> users = new HashMap<>();

    public User addUser(UserDto userDto) {
        if (StringUtils.isBlank(userDto.getEmail())) {
            throw new ValidationException("У пользователя отсутствует почта!");
        }
        searchByMail(userDto.getEmail());
        User user = userMapper.toUser(userDto);
        Integer id = generateId();
        user.setId(id);
        users.put(id, user);
        log.info("Добавлен пользователь с {}", id);
        return user;
    }

    public User updateUser(UserDto userDto, Integer userId) {
        User user = users.get(userId);

        if (user == null) {
            log.error("Пользователь с id {} не найден.", userId);
            throw new NotFoundException("Пользователь с id " + userId + " не найден.");
        }

        if (userDto.getEmail() != null) {
            searchByMail(userDto.getEmail());
            user.setEmail(userDto.getEmail());
        }

        if (userDto.getName() != null) {
            user.setName(userDto.getName());
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

    public void searchByMail(String email) {
        boolean emailExists = users.values().stream()
                .anyMatch(user -> user.getEmail().equals(email));

        if (emailExists) {
            throw new ConflictException("Такая почта уже существует " + email);
        }
    }
}
