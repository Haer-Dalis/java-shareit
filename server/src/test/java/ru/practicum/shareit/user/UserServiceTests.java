package ru.practicum.shareit.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceTests {

    @Autowired
    private final EntityManager entityManager;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

    @Test
    void findByIdSuccessTesting() {
        UserDto userDto = UserDto.builder()
                .name("Ivan")
                .email("Ivan@tupopochta.ru")
                .build();

        UserDto createdUser = userService.addUser(userDto);
        UserDto foundUser = userService.getUserById(createdUser.getId());

        assertNotNull(foundUser);
        assertEquals(createdUser.getId(), foundUser.getId());
        assertEquals("Ivan", foundUser.getName());
        assertEquals("Ivan@tupopochta.ru", foundUser.getEmail());
    }

    @Test
    void addUserTesting() {
        UserDto userDto = UserDto.builder()
                .name("Ivan")
                .email("Ivan@tupopochta.ru")
                .build();

        UserDto createdUser = userService.addUser(userDto);

        TypedQuery<User> query = entityManager.createQuery("Select u from User u where u.id = :id", User.class);
        User user = query.setParameter("id", createdUser.getId())
                .getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void updateRealUserSuccessTesting() {
        UserDto userDto = UserDto.builder()
                .name("Ivan")
                .email("Ivan@tupopochta.ru")
                .build();

        UserDto createdUser = userService.addUser(userDto);

        UserDto updateUserDto = UserDto.builder()
                .name("Updated Ivan")
                .email("updated.ivan@tupopochta.ru")
                .build();

        UserDto updatedUser = userService.updateUser(updateUserDto, createdUser.getId());

        assertEquals("Updated Ivan", updatedUser.getName());
        assertEquals("updated.ivan@tupopochta.ru", updatedUser.getEmail());
    }

    @Test
    void deleteByIdTesting() {
        UserDto userDto = UserDto.builder()
                .name("Ivan")
                .email("Ivan@tupopochta.ru")
                .build();

        UserDto userToDelete = userService.addUser(userDto);

        userService.deleteUser(userToDelete.getId());

        Optional<User> deletedUser = userRepository.findById(userToDelete.getId());
        assertTrue(deletedUser.isEmpty());
    }

    @Test
    void findByIdFailureTesting() {
        NotFoundException n =
                assertThrows(NotFoundException.class, () -> userService.getUserById(1024));
        assertEquals("Пользователь с id 1024 не найден.", n.getMessage());
    }
}