package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.repository.InMemoryUserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final InMemoryUserRepository inMemoryUserRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto addUser(UserDto userDto) {
        User user = userMapper.toUser(userDto);
        return userMapper.toUserDto(inMemoryUserRepository.addUser(user));
    }

    @Override
    public UserDto updateUser(UserDto userDto, Integer userId) {
        User user = userMapper.toUser(userDto);
        return userMapper.toUserDto(inMemoryUserRepository.updateUser(user, userId));
    }

    @Override
    public UserDto deleteUser(Integer userId) {
        return userMapper.toUserDto(inMemoryUserRepository.deleteUser(userId));
    }

    @Override
    public List<UserDto> getUsers() {
        return inMemoryUserRepository.getUsers()
                .stream()
                .map(userMapper::toUserDto)
                .toList();
    }

    @Override
    public UserDto getUserById(Integer userId) {
        return userMapper.toUserDto(inMemoryUserRepository.getUser(userId));
    }
}