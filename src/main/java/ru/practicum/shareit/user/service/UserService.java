package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto addUser(UserDto userDto);

    UserDto updateUser(UserDto userDto, Integer userId);

    UserDto deleteUser(Integer userId);

    List<UserDto> getUsers();

    UserDto getUserById(Integer userId);
}
