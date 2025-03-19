package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestOutputDto;

import java.util.List;

@Slf4j
@Service
public class ItemRequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    @Autowired
    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ItemRequestOutputDto addRequest(long userId, ItemRequestDto item) {
        log.info("Отправка запроса в Service: userId={}, item={}", userId, item);
        ItemRequestOutputDto response = postRequest("", userId, item).getBody();
        log.info("Ответ от сервера в Service: {}", response);
        return response;
    }

    public List<ItemRequestOutputDto> getAllRequests(long userId) {
        return getList("", userId, ItemRequestOutputDto.class).getBody();
    }

    public List<ItemRequestOutputDto> getAllByUser(long userId) {
        return getList("/all", userId, ItemRequestOutputDto.class).getBody();
    }

    public ItemRequestOutputDto getItemRequest(long userId, long requestId) {
        return getItemRequest("/" + requestId, userId).getBody();
    }
}