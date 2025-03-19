package ru.practicum.shareit.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
public class BaseClient {
    protected final RestTemplate rest;

    public BaseClient(RestTemplate rest) {
        this.rest = rest;
    }

    protected <T> ResponseEntity<T> get(String path, long userId, Class<T> responseType) {
        return makeAndSendRequest(HttpMethod.GET, path, userId, null, null, responseType);
    }

    protected <T> ResponseEntity<List<T>> getList(String path, long userId, Class<T> responseType) {
        return makeAndSendRequest(HttpMethod.GET, path, userId, null, null, new ParameterizedTypeReference<List<T>>() {});
    }

    protected <T, R> ResponseEntity<T> post(String path, long userId, R body, Class<T> responseType) {
        return makeAndSendRequest(HttpMethod.POST, path, userId, null, body, responseType);
    }

    protected <T, R> ResponseEntity<T> post(String path, long userId, @Nullable Map<String, Object> parameters, R body, Class<T> responseType) {
        return makeAndSendRequest(HttpMethod.POST, path, userId, parameters, body, responseType);
    }

    protected <T, R> ResponseEntity<T> patch(String path, long userId, @Nullable Map<String, Object> parameters, R body, Class<T> responseType) {
        return makeAndSendRequest(HttpMethod.PATCH, path, userId, parameters, body, responseType);
    }

    protected ResponseEntity<Void> delete(String path, long userId) {
        return makeAndSendRequest(HttpMethod.DELETE, path, userId, null, null, Void.class);
    }

    protected ResponseEntity<Void> delete(String path, long userId, @Nullable Map<String, Object> parameters) {
        return makeAndSendRequest(HttpMethod.DELETE, path, userId, parameters, null, Void.class);
    }

    protected <T> ResponseEntity<List<T>> getListSearch(String path, @Nullable Map<String, Object> parameters, Class<T> responseType) {
        return makeAndSendRequest(HttpMethod.GET, path, null, parameters, null, new ParameterizedTypeReference<List<T>>() {});
    }

    protected <T> ResponseEntity<T> deleteUs(String path, Class<T> responseType) {
        return makeAndSendRequest(HttpMethod.DELETE, path, null, null, null, responseType);
    }

    protected <T, R> ResponseEntity<T> postUser(String path, R body, Class<T> responseType) {
        return postUser(path, null, null, body, responseType);
    }

    protected <T, R> ResponseEntity<T> postUser(String path, Long userId, @Nullable Map<String, Object> parameters, R body, Class<T> responseType) {
        return makeAndSendRequest(HttpMethod.POST, path, userId, parameters, body, responseType);
    }

    protected <T, R> ResponseEntity<T> patchUser(String path, long userId, R body, Class<T> responseType) {
        return patchUser(path, userId, null, body, responseType);
    }

    protected <T, R> ResponseEntity<T> patchUser(String path, Long userId, @Nullable Map<String, Object> parameters, R body, Class<T> responseType) {
        return makeAndSendRequest(HttpMethod.PATCH, path, userId, parameters, body, responseType);
    }

    private <T, R> ResponseEntity<T> makeAndSendRequest(HttpMethod method, String path, Long userId,
                                                        @Nullable Map<String, Object> parameters,
                                                        @Nullable R body, Class<T> responseType) {
        HttpEntity<R> requestEntity = new HttpEntity<>(body, defaultHeaders(userId));

        try {
            if (parameters != null) {
                return rest.exchange(path, method, requestEntity, responseType, parameters);
            } else {
                return rest.exchange(path, method, requestEntity, responseType);
            }
        } catch (HttpStatusCodeException e) {
            log.error("Error while executing request: {}", e.getMessage());
            return ResponseEntity.status(e.getStatusCode()).body(null);
        }
    }

    private <T> ResponseEntity<List<T>> makeAndSendRequest(HttpMethod method, String path, Long userId,
                                                           @Nullable Map<String, Object> parameters,
                                                           @Nullable Object body,
                                                           ParameterizedTypeReference<List<T>> responseType) {
        HttpEntity<Object> requestEntity = new HttpEntity<>(body, defaultHeaders(userId));

        try {
            if (parameters != null) {
                return rest.exchange(path, method, requestEntity, responseType, parameters);
            } else {
                return rest.exchange(path, method, requestEntity, responseType);
            }
        } catch (HttpStatusCodeException e) {
            log.error("Error while executing request: {}", e.getMessage());
            return ResponseEntity.status(e.getStatusCode()).body(null);
        }
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
}