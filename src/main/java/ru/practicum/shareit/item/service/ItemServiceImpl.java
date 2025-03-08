package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final ItemMapper itemMapper;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public ItemDto addItem(Integer userId, ItemDto itemDto) {
        Item item = itemMapper.toItem(itemDto);
        User user = userMapper.toUser(userService.getUserById(userId));
        item.setOwner(user);
        log.info("Добавлен предмет: {}", item);
        return itemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public ItemDto editItem(Integer userId, Integer itemId, ItemDto itemDto) {
        userService.getUserById(userId);
        Item existingItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет с id " + itemId + " не найден"));
        if (!existingItem.getOwner().getId().equals(userId)) {
            throw new AccessDeniedException("Попытка несанкционированного доступа");
        }
        if (itemDto.getName() != null) {
            existingItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            existingItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            existingItem.setAvailable(itemDto.getAvailable());
        }
        itemRepository.save(existingItem);
        log.info("Обновлен предмет: {}", existingItem);
        return itemMapper.toItemDto(existingItem);
    }

    @Override
    public ItemDto getItem(Integer itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Не найден предмет c id: %s", itemId)));
        return itemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getItemsOfUser(Integer userId) {
        userService.getUserById(userId);
        return itemRepository.findByOwnerId(userId).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String searchQuery) {
        if (searchQuery.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.search(searchQuery).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteItem(Integer ownerId, Integer itemId) {
        userService.getUserById(ownerId);
        Item item = itemMapper.toItem(getItem(itemId));
        if (item.getOwner().getId().equals(ownerId)) {
            throw new NotFoundException(
                    String.format("У пользователя %s не найден предмет c id: %s", ownerId, itemId)
            );
        }
        itemRepository.deleteById(itemId);
        log.info("Удален предмет с id: {} у пользователя с id: {}", itemId, ownerId);
    }

    @Override
    @Transactional
    public CommentOutputDto addComment(Integer userId, CommentDto commentDto, Integer itemId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден: " + userId));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет не найден: " + itemId));
        bookingRepository.findFirstByItemIdAndBookerIdAndStatusAndEndBefore(
                        itemId, userId, Status.APPROVED, LocalDateTime.now())
                .orElseThrow(() -> new ValidationException("Пользователь " + userId + " не бронировал этот предмет"));
        Comment comment = CommentMapper.toComment(commentDto);
        comment.setCreated(LocalDateTime.now());
        comment.setItem(item);
        comment.setAuthor(user);
        log.info("Добавлен комментарий: {}", comment);
        return CommentMapper.toCommentOutputDto(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public ItemOutputDto addBookingInfoAndComments(Integer itemId, Integer userId) {
        log.info("Обработка запроса: itemId={}, userId={}", itemId, userId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет не найден: " + itemId));
        ItemOutputDto itemOutputDto = ItemMapper.toItemOutputDto(item);
        LocalDateTime currentTime = LocalDateTime.now();
        if (item.getOwner() != null && item.getOwner().getId().equals(userId)) {
            log.info("Пользователь {} является владельцем предмета {}", userId, itemId);
            bookingRepository.findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(itemId, Status.APPROVED, currentTime)
                    .ifPresent(booking -> {
                        itemOutputDto.setLastBooking(BookingMapper.toBookingShortDto(booking));
                        log.info("Нашел предыдущее бронирование {}", booking);
                    });

            bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(itemId, Status.APPROVED, currentTime)
                    .ifPresent(booking -> {
                        itemOutputDto.setNextBooking(BookingMapper.toBookingShortDto(booking));
                        log.info("Наше следующее бронирование: {}", booking);
                    });
        } else {
            log.info("Пользователь {} не является собственником предмета {}", userId, itemId);
        }
        List<Comment> comments = commentRepository.findAllByItemId(itemId);
        if (!comments.isEmpty()) {
            itemOutputDto.setComments(CommentMapper.toCommentOutDtoList(comments));
            log.info("Найдено {} комментариев", comments.size());
        } else {
            itemOutputDto.setComments(Collections.emptyList());
        }

        return itemOutputDto;
    }

}
