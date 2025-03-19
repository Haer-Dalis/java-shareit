package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserClient userClient;

    @PostMapping
    public UserDto addUser(@Valid @RequestBody UserDto userDto) {
        log.info("Запрос на создание пользователя с id: {}", userDto.getId());
        return userClient.addUser(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@RequestBody UserDto userDto, @Positive @PathVariable Long userId) {
        log.info("Запрос на обновление пользователя: {}", userId);
        return userClient.updateUser(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public UserDto deleteUser(@Positive @PathVariable Long userId) {
        log.info("Запрос на удаление пользователя с id: {}", userId);
        return userClient.deleteUser(userId);
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@Positive @PathVariable Long userId) {
        log.info("Запрос на пользователя по id: {}", userId);
        return userClient.getUser(userId);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("Запрос на всех пользователей");
        return userClient.getAllUsers();
    }
}

