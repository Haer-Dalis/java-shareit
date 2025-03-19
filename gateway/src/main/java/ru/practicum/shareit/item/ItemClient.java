package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentOutputDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOutputDto;

import java.util.List;
import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }


    public ItemDto addItem(long userId, ItemDto itemDto) {
        return post("", userId, itemDto, ItemDto.class).getBody();
    }

    public ItemDto updateItem(long userId, long itemId, ItemDto itemDto) {
        return patch("/" + itemId, userId, null, itemDto, ItemDto.class).getBody();
    }

    public ItemOutputDto getItem(long userId, long itemId) {
        return get("/" + itemId, userId, ItemOutputDto.class).getBody();
    }

    public List<ItemDto> getAllItems(long userId) {
        return getList("", userId, ItemDto.class).getBody();
    }

    public List<ItemDto> searchItems(String text) {
        return getListSearch("/search", Map.of("text", text), ItemDto.class).getBody();
    }

    public CommentOutputDto createComment(long userId, long itemId, CommentDto commentDto) {
        return post("/" + itemId + "/comment", userId, commentDto, CommentOutputDto.class).getBody();
    }
}

