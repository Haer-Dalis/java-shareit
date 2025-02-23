package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto addUser(UserDto userDto) {
        if (userDto.getEmail() == null || userDto.getEmail().isBlank()) {
            throw new ValidationException("У пользователя отсутствует почта!");
        }

        checkEmail(userDto.getEmail(), null);

        User user = userMapper.toUser(userDto);

        try {
            return UserMapper.toUserDto(userRepository.save(user));
        } catch (DataIntegrityViolationException error) {
            throw new ConflictException("Пользователь с таким e-mail уже существует");
        }
    }

    @Override
    public UserDto updateUser(UserDto userDto, Integer userId) {
        User oldUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден."));

        if (userDto.getEmail() != null && !userDto.getEmail().equals(oldUser.getEmail())) {
            checkEmail(userDto.getEmail(), userId);
            oldUser.setEmail(userDto.getEmail());
        }

        if (userDto.getName() != null) {
            oldUser.setName(userDto.getName());
        }

        log.debug("Пользователь с id {} успешно обновлен", userId);

        userRepository.save(oldUser);
        return UserMapper.toUserDto(oldUser);
    }

    @Override
    public UserDto deleteUser(Integer userId) {
        UserDto userDto = getUserById(userId);
        userRepository.deleteById(userId);
        log.info("Удален пользователь с id {} ", userId);
        return userDto;
    }

    @Override
    public List<UserDto> getUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toUserDto)
                .toList();
    }

    @Override
    public UserDto getUserById(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден."));
        return UserMapper.toUserDto(user);
    }

    private void checkEmail(String email, Integer userId) {
        if (userRepository.existsByEmailAndIdNot(email, userId)) {
            throw new ConflictException("Пользователь с таким e-mail уже существует");
        }
    }
}