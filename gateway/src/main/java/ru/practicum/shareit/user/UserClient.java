package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Service
public class UserClient extends BaseClient {
    private static final String API_PREFIX = "/users";

    @Autowired
    public UserClient(@Value("${shareit-server.url}") String serverUrl,
                      RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public UserDto addUser(UserDto userDto) {
        return postUser("", userDto, UserDto.class).getBody();
    }

    public UserDto updateUser(Long userId, UserDto userDto) {
        return patchUser("/" + userId, userId, userDto, UserDto.class).getBody();
    }

    public UserDto deleteUser(Long userId) {
        return deleteUs("/" + userId, UserDto.class).getBody();
    }

    public UserDto getUser(Long userId) {
        return get("/" + userId, userId, UserDto.class).getBody();
    }

    public List<UserDto> getAllUsers() {
        return getList("", 0L, UserDto.class).getBody();
    }

}
