package ru.practicum.shareit.request;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestOutputDto;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RequestServiceTests {

    @Autowired
    private final EntityManager entityManager;
    UserDto user1;
    UserDto user2;
    UserDto user3;
    TypedQuery<ItemRequest> queryFindById;
    @Autowired
    private ItemRequestRepository itemRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private ItemRequestService itemRequestService;


    @BeforeEach
    void setUp() {
        UserDto userDto1 = UserDto.builder()
                .name("Ivan1")
                .email("ivanovapochta1@bk.ru")
                .build();

        UserDto userDto2 = UserDto.builder()
                .name("Ivan2")
                .email("ivanovapochta2@bk.ru")
                .build();

        UserDto userDto3 = UserDto.builder()
                .name("Ivan3")
                .email("ivanovapochta3@bk.ru")
                .build();

        user1 = userService.addUser(userDto1);
        user2 = userService.addUser(userDto2);
        user3 = userService.addUser(userDto3);

        ItemDto itemDto1 = ItemDto.builder()
                .name("Item 1")
                .description("The description of Item 1")
                .available(true)
                .build();

        ItemDto itemDto2 = ItemDto.builder()
                .name("Item 2")
                .description("The description of Item 2")
                .available(true)
                .build();

        ItemDto itemDto3 = ItemDto.builder()
                .name("Item 3")
                .description("The description of Item 3")
                .available(true)
                .build();

        queryFindById = entityManager.createQuery("Select r from ItemRequest r where r.id = :id", ItemRequest.class);
    }

    @Test
    void addRequestTesting() {
        ItemRequestDto newRequest = ItemRequestDto.builder()
                .description("The description of Request 1")
                .build();

        ItemRequestOutputDto created = itemRequestService.addRequest(newRequest, user1.getId());
        ItemRequest request = queryFindById.setParameter("id", created.getId()).getSingleResult();

        assertNotNull(request);
        assertNotNull(request.getId());
        assertEquals(created.getDescription(), request.getDescription());
        assertEquals(created.getCreated(), request.getCreated());
    }

    @Test
    void getRequestTesting() {
        ItemRequestDto newRequest = ItemRequestDto.builder()
                .description("The description of Request 1")
                .build();

        ItemRequestOutputDto created = itemRequestService.addRequest(newRequest, user1.getId());
        ItemRequestOutputDto found = itemRequestService.getRequest(user1.getId(), created.getId());
        assertNotNull(found);
        assertEquals(created.getDescription(), found.getDescription());
        assertEquals(created.getCreated(), found.getCreated());
    }

    @Test
    void getAllRequestsTesting() {
        ItemRequestDto newRequest1 = ItemRequestDto.builder()
                .description("The description of Request 1")
                .build();
        ItemRequestDto newRequest2 = ItemRequestDto.builder()
                .description("The description of Request 2")
                .build();

        ItemRequestOutputDto requestOne = itemRequestService.addRequest(newRequest1, user1.getId());
        ItemRequestOutputDto requestTwo = itemRequestService.addRequest(newRequest2, user1.getId());

        List<ItemRequestOutputDto> items = itemRequestService.getUserRequests(user1.getId());

        assertNotNull(items);
        assertEquals(2, items.size());

        ItemRequestOutputDto request1 = items.get(0);
        ItemRequestOutputDto request2 = items.get(1);

        assertEquals(requestOne.getDescription(), request1.getDescription());
        assertEquals(requestOne.getCreated(), request1.getCreated());
        assertEquals(requestTwo.getDescription(), request2.getDescription());
        assertEquals(requestTwo.getCreated(), request2.getCreated());
    }

    @Test
    void getAllRequestsExceptOneUserTesting() {
        ItemRequestDto newRequest1 = ItemRequestDto.builder()
                .description("The description of Request 1")
                .build();
        ItemRequestDto newRequest2 = ItemRequestDto.builder()
                .description("The description of Request 2")
                .build();
        ItemRequestDto newRequest3 = ItemRequestDto.builder()
                .description("The description of Request 3")
                .build();

        ItemRequestOutputDto requestOne = itemRequestService.addRequest(newRequest1, user1.getId());
        ItemRequestOutputDto requestTwo = itemRequestService.addRequest(newRequest2, user2.getId());
        ItemRequestOutputDto requestThree = itemRequestService.addRequest(newRequest3, user3.getId());

        List<ItemRequestOutputDto> items = itemRequestService.getAllRequestsExceptOneUser(user3.getId());

        assertNotNull(items);
        assertEquals(2, items.size());

        ItemRequestOutputDto request1 = items.get(0);
        ItemRequestOutputDto request2 = items.get(1);

        assertEquals(requestOne.getDescription(), request1.getDescription());
        assertEquals(requestOne.getCreated(), request1.getCreated());
        assertEquals(requestTwo.getDescription(), request2.getDescription());
        assertEquals(requestTwo.getCreated(), request2.getCreated());
    }

}