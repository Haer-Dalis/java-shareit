package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestOutputDto;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserService userService;
    private final ItemService itemService;
    private final UserMapper userMapper;
    private final ItemMapper itemMapper;

    @Override
    @Transactional
    public ItemRequestOutputDto addRequest(ItemRequestDto itemRequestDto, Integer userId) {
        log.info("Получен запрос на добавление запроса от пользователя с id: {}", userId);
        User user = userMapper.toUser(userService.getUserById(userId));
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
        itemRequest.setRequester(user);
        itemRequest.setCreated(LocalDateTime.now());
        try {
            itemRequest = itemRequestRepository.save(itemRequest);
            log.info("Запрос успешно сохранен в базу данных: {}", itemRequest);
        } catch (Exception e) {
            log.error("Ошибка при сохранении запроса в базу данных: {}", e.getMessage(), e);
            throw new RuntimeException("Ошибка при сохранении запроса", e);
        }
        return ItemRequestMapper.toItemRequestOutDto(itemRequest);
    }

    @Override
    public List<ItemRequestOutputDto> getUserRequests(Integer userId) {
        userService.getUserById(userId);
        List<ItemRequest> itemRequests = itemRequestRepository.findByRequesterIdOrderByCreatedAsc(userId);
        return itemRequests.stream()
                .map(this::toItemRequestOutputDtoWithItems)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestOutputDto> getAllRequestsExceptOneUser(Integer userId) {
        userService.getUserById(userId);
        return itemRequestRepository.findByRequesterIdNotOrderByCreatedAsc(userId).stream()
                .map(this::toItemRequestOutputDtoWithItems)
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestOutputDto getRequest(Integer userId, Integer requestId) {
        userService.getUserById(userId);
        ItemRequest request = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(String.format("Не найден запрос c id: %s", requestId)));
        return toItemRequestOutputDtoWithItems(request);
    }

    @Override
    public ItemRequestOutputDto toItemRequestOutputDtoWithItems(ItemRequest itemRequest) {
        ItemRequestOutputDto dto = ItemRequestMapper.toItemRequestOutDto(itemRequest);
        List<ItemDto> itemDtoList = itemService.getByRequestId(itemRequest.getId()).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
        dto.setItems(itemDtoList);
        return dto;
    }

}
