package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestOutputDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestOutputDto addRequest(ItemRequestDto itemRequestDto, Integer userId);

    List<ItemRequestOutputDto> getUserRequests(Integer userId);

    List<ItemRequestOutputDto> getAllRequestsExceptOneUser(Integer userId);

    ItemRequestOutputDto getRequest(Integer userId, Integer requestId);

    ItemRequestOutputDto toItemRequestOutputDtoWithItems(ItemRequest itemRequest);

}
