package ru.practicum.shareit.client;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.item.dto.CommentOutputDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOutputDto;
import ru.practicum.shareit.request.dto.ItemRequestOutputDto;
import ru.practicum.shareit.user.dto.UserDto;

@Slf4j
public class BaseClient {
    protected final RestTemplate rest;

    public BaseClient(RestTemplate rest) {
        this.rest = rest;
    }

    private <T, R> ResponseEntity<T> makeAndSendRequestNew(HttpMethod method, String path, Long userId,
                                                           @Nullable Map<String, Object> parameters,
                                                           @Nullable R body, Class<T> responseType) {
        HttpEntity<R> requestEntity = new HttpEntity<>(body, defaultHeaders(userId));

        ResponseEntity<T> response;
        try {
            if (parameters != null) {
                response = rest.exchange(path, method, requestEntity, responseType, parameters);
            } else {
                response = rest.exchange(path, method, requestEntity, responseType);
            }
        } catch (HttpStatusCodeException e) {
            log.error("Error while executing request: {}", e.getMessage());

            if (responseType.isAssignableFrom(byte[].class)) {
                return ResponseEntity.status(e.getStatusCode())
                        .headers(e.getResponseHeaders())
                        .body(responseType.cast(e.getResponseBodyAsByteArray()));
            }

            return ResponseEntity.status(e.getStatusCode()).body(null);
        }

        return ResponseEntity.status(response.getStatusCode())
                .headers(response.getHeaders())
                .body(response.getBody());
    }

    private <T> ResponseEntity<List<T>> makeAndSendRequestNew(HttpMethod method, String path, Long userId,
                                                              @Nullable Map<String, Object> parameters,
                                                              @Nullable Object body,
                                                              ParameterizedTypeReference<List<T>> responseType) {
        HttpEntity<Object> requestEntity = new HttpEntity<>(body, defaultHeaders(userId));

        ResponseEntity<List<T>> response;
        try {
            if (parameters != null) {
                response = rest.exchange(path, method, requestEntity, responseType, parameters);
            } else {
                response = rest.exchange(path, method, requestEntity, responseType);
            }
        } catch (HttpStatusCodeException e) {
            log.error("Error while executing request: {}", e.getMessage());
            return ResponseEntity.status(e.getStatusCode()).body(Collections.emptyList());
        }
        return prepareGatewayResponseList(response);
    }

    private <T> ResponseEntity<List<T>> prepareGatewayResponseList(ResponseEntity<List<T>> response) {
        HttpHeaders headers = response.getHeaders();
        List<T> body = response.getBody() != null ? response.getBody() : Collections.emptyList();
        return new ResponseEntity<>(body, headers, response.getStatusCode());
    }

    protected ResponseEntity<ItemOutputDto> getItem(String path, long userId) {
        return makeAndSendRequestNew(HttpMethod.GET, path, userId, null, null, ItemOutputDto.class);
    }

    protected ResponseEntity<BookingOutputDto> patchBooking(String path, Long userId, @Nullable Map<String, Object> parameters) {
        return makeAndSendRequestNew(HttpMethod.PATCH, path, userId, parameters, null, BookingOutputDto.class);
    }

    protected ResponseEntity<BookingOutputDto> getBooking(String path, long userId) {
        ResponseEntity<BookingOutputDto> response = makeAndSendRequestNew(HttpMethod.GET, path, userId, null, null, BookingOutputDto.class);
        log.info("GET Booking request to {} for user {} returned: {}", path, userId, response);
        return response;
    }

    protected <R> ResponseEntity<BookingOutputDto> postBooking(String path, long userId, R body) {
        log.info("Запрос postBooking: path={}, userId={}, body={}", path, userId, body);
        ResponseEntity<BookingOutputDto> response = makeAndSendRequestNew(HttpMethod.POST, path, userId, null, body, BookingOutputDto.class);
        log.info("Результат postBooking post: статус={}, тело={}", response.getStatusCode(), response.getBody());
        return response;
    }

    protected <T> ResponseEntity<List<T>> getList(String path, long userId, Class<T> responseType) {
        return makeAndSendRequestNew(HttpMethod.GET, path, userId, null, null, new ParameterizedTypeReference<List<T>>() {});
    }

    protected ResponseEntity<List<ItemDto>> getListItems(String path, long userId) {
        return makeAndSendRequestNew(HttpMethod.GET, path, userId, null, null, new ParameterizedTypeReference<List<ItemDto>>() {});
    }

    protected <T> ResponseEntity<List<T>> getListSearch(String path, @Nullable Map<String, Object> parameters, Class<T> responseType) {
        return makeAndSendRequestNew(HttpMethod.GET, path, null, parameters, null, new ParameterizedTypeReference<List<T>>() {});
    }

    protected <R> ResponseEntity<ItemDto> postItem(String path, long userId, R body) {
        return makeAndSendRequestNew(HttpMethod.POST, path, userId, null, body, ItemDto.class);
    }

    protected <R> ResponseEntity<ItemDto> patchItem(String path, long userId, @Nullable Map<String, Object> parameters, R body) {
        return makeAndSendRequestNew(HttpMethod.PATCH, path, userId, parameters, body, ItemDto.class);
    }

    protected <T> ResponseEntity<CommentOutputDto> postComment(String path, Long userId, T body) {
        return makeAndSendRequestNew(HttpMethod.POST, path, userId, null, body, CommentOutputDto.class);
    }

    protected <R> ResponseEntity<UserDto> postUser(String path, R body) {
        return makeAndSendRequestNew(HttpMethod.POST, path, null, null, body, UserDto.class);
    }

    protected <R> ResponseEntity<UserDto> patchUser(String path, long userId, R body) {
        return makeAndSendRequestNew(HttpMethod.PATCH, path, userId, null, body, UserDto.class);
    }

    protected ResponseEntity<UserDto> getUser(String path, long userId) {
        return makeAndSendRequestNew(HttpMethod.GET, path, userId, null, null, UserDto.class);
    }

    protected <T> ResponseEntity<UserDto> deleteUs(String path) {
        return makeAndSendRequestNew(HttpMethod.DELETE, path, null, null, null, UserDto.class);
    }

    protected <R> ResponseEntity<ItemRequestOutputDto> postRequest(String path, Long userId, R body) {
        return makeAndSendRequestNew(HttpMethod.POST, path, userId, null, body, ItemRequestOutputDto.class);
    }

    protected ResponseEntity<ItemRequestOutputDto> getItemRequest(String path, long userId) {
        ResponseEntity<ItemRequestOutputDto> response = makeAndSendRequestNew(HttpMethod.GET, path, userId, null, null, ItemRequestOutputDto.class);
        return response;
    }

    protected ResponseEntity<Object> get(String path) {
        return get(path, null, null);
    }

    protected ResponseEntity<Object> get(String path, long userId) {
        return get(path, userId, null);
    }

    protected ResponseEntity<Object> get(String path, Long userId, @Nullable Map<String, Object> parameters) {
        return makeAndSendRequest(HttpMethod.GET, path, userId, parameters, null);
    }

    protected <T> ResponseEntity<Object> post(String path, T body) {
        return post(path, null, null, body);
    }

    protected <T> ResponseEntity<Object> post(String path, long userId, T body) {
        log.info("Вызов метода post. path={}, userId={}, body={}", path, userId, body);
        ResponseEntity<Object> response = post(path, userId, null, body);
        log.info("Результат метода post: {}", response);
        return response;
    }

    protected <T> ResponseEntity<Object> post(String path, Long userId, @Nullable Map<String, Object> parameters, T body) {
        return makeAndSendRequest(HttpMethod.POST, path, userId, parameters, body);
    }

    protected <T> ResponseEntity<Object> put(String path, long userId, T body) {
        return put(path, userId, null, body);
    }

    protected <T> ResponseEntity<Object> put(String path, long userId, @Nullable Map<String, Object> parameters, T body) {
        return makeAndSendRequest(HttpMethod.PUT, path, userId, parameters, body);
    }

    protected <T> ResponseEntity<Object> patch(String path, T body) {
        return patch(path, null, null, body);
    }

    protected <T> ResponseEntity<Object> patch(String path, long userId) {
        return patch(path, userId, null, null);
    }

    protected <T> ResponseEntity<Object> patch(String path, long userId, T body) {
        return patch(path, userId, null, body);
    }

    protected <T> ResponseEntity<Object> patch(String path, Long userId, @Nullable Map<String, Object> parameters, T body) {
        return makeAndSendRequest(HttpMethod.PATCH, path, userId, parameters, body);
    }

    protected ResponseEntity<Object> delete(String path) {
        return delete(path, null, null);
    }

    protected ResponseEntity<Object> delete(String path, long userId) {
        return delete(path, userId, null);
    }

    protected ResponseEntity<Object> delete(String path, Long userId, @Nullable Map<String, Object> parameters) {
        return makeAndSendRequest(HttpMethod.DELETE, path, userId, parameters, null);
    }

    private <T> ResponseEntity<Object> makeAndSendRequest(HttpMethod method, String path, Long userId, @Nullable Map<String, Object> parameters, @Nullable T body) {
        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders(userId));

        ResponseEntity<Object> shareitServerResponse;
        try {
            if (parameters != null) {
                shareitServerResponse = rest.exchange(path, method, requestEntity, Object.class, parameters);
            } else {
                shareitServerResponse = rest.exchange(path, method, requestEntity, Object.class);
            }
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }
        return prepareGatewayResponse(shareitServerResponse);
    }

    private HttpHeaders defaultHeaders(Long userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        if (userId != null) {
            headers.set("X-Sharer-User-Id", String.valueOf(userId));
        }
        return headers;
    }

    private static ResponseEntity<Object> prepareGatewayResponse(ResponseEntity<Object> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }

        return responseBuilder.build();
    }
}