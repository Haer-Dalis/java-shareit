package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemOutputDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ItemMapperTest {

    private final ItemMapper itemMapper = new ItemMapper();

    @Test
    void mapItemDtoToItemTesting() {
        ItemDto itemDto = ItemDto.builder()
                .id(1)
                .name("Item Name")
                .description("Item Description")
                .available(true)
                .build();

        Item item = itemMapper.toItem(itemDto);

        assertNotNull(item);
        assertEquals(itemDto.getId(), item.getId());
        assertEquals(itemDto.getName(), item.getName());
        assertEquals(itemDto.getDescription(), item.getDescription());
        assertEquals(itemDto.getAvailable(), item.getAvailable());
    }

    @Test
    void mapItemToItemDtoWithoutRequestTesting() {
        Item item = Item.builder()
                .id(1)
                .name("Item Name")
                .description("Item Description")
                .available(true)
                .build();

        ItemDto itemDto = itemMapper.toItemDto(item);

        assertNotNull(itemDto);
        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.getAvailable(), itemDto.getAvailable());
        assertNull(itemDto.getRequestId());
    }

    @Test
    void mapItemToItemDtoWithRequestTesting() {
        ItemRequest itemRequest = ItemRequest.builder().id(10).build();
        Item item = Item.builder()
                .id(1)
                .name("Item Name")
                .description("Item Description")
                .available(true)
                .request(itemRequest)
                .build();

        ItemDto itemDto = itemMapper.toItemDto(item);

        assertNotNull(itemDto);
        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.getAvailable(), itemDto.getAvailable());
        assertEquals(itemRequest.getId(), itemDto.getRequestId());
    }

    @Test
    void mapItemToItemOutputDtoTesting() {
        Item item = Item.builder()
                .id(1)
                .name("Item Name")
                .description("Item Description")
                .available(true)
                .build();

        ItemOutputDto itemOutputDto = ItemMapper.toItemOutputDto(item);

        assertNotNull(itemOutputDto);
        assertEquals(item.getId(), itemOutputDto.getId());
        assertEquals(item.getName(), itemOutputDto.getName());
        assertEquals(item.getDescription(), itemOutputDto.getDescription());
        assertEquals(item.getAvailable(), itemOutputDto.getAvailable());
    }
}
