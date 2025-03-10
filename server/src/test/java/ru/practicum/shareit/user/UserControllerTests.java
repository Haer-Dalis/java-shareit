package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTests {

    @InjectMocks
    private UserController controller;

    @Mock
    private UserService service;

    @Autowired
    private MockMvc mvc;

    private ObjectMapper objectMapper;

    private UserDto userDto;
    private UserDto userDtoWithoutId;
    private UserDto updateUserDto;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mvc = MockMvcBuilders.standaloneSetup(controller).build();

        userDto = UserDto.builder()
                .id(1)
                .name("Ivan Malchik")
                .email("ivan.malchik@gmail.com")
                .build();

        userDtoWithoutId = UserDto.builder()
                .name("Ivan Malchik")
                .email("ivan.malchik@gmail.com")
                .build();

        updateUserDto = UserDto.builder()
                .name("Updated Ivan")
                .email("updated.ivan.malchik@gmail.com")
                .build();
    }

    @Test
    void addUserTesting() throws Exception {
        when(service.addUser(any(UserDto.class))).thenReturn(userDto);

        mvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDtoWithoutId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userDto.getId()))
                .andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()));
    }

    @Test
    void updateUserTesting() throws Exception {
        when(service.updateUser(any(UserDto.class), eq(1))).thenReturn(userDto);

        mvc.perform(patch("/users/1")
                        .content(objectMapper.writeValueAsString(updateUserDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userDto.getId()))
                .andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()));
    }

    @Test
    void deleteUserTesting() throws Exception {
        mvc.perform(delete("/users/1"))
                .andExpect(status().isOk());

        verify(service, times(1)).deleteUser(1);
    }

    @Test
    void getUserByIdTesting() throws Exception {
        when(service.getUserById(1)).thenReturn(userDto);

        mvc.perform(get("/users/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userDto.getId()))
                .andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()));
    }
}