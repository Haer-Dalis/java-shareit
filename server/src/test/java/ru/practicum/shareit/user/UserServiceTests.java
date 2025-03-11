package ru.practicum.shareit.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
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
    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        user = new User(1, "John Doe", "john.doe@example.com");
        userDto = UserDto.builder().id(1).name("John Doe").email("john.doe@example.com").build();
    }

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
    void updateUserPartialNameChangeTesting() {
        UserDto userDto = UserDto.builder()
                .name("Ivan")
                .email("Ivan@tupopochta.ru")
                .build();

        UserDto createdUser = userService.addUser(userDto);

        UserDto updateUserDto = UserDto.builder()
                .name("New Ivan")
                .email(null)
                .build();

        UserDto updatedUser = userService.updateUser(updateUserDto, createdUser.getId());

        assertEquals("New Ivan", updatedUser.getName());
        assertEquals("Ivan@tupopochta.ru", updatedUser.getEmail());
    }

    @Test
    void updateUserPartialEmailChangeTesting() {
        UserDto userDto = UserDto.builder()
                .name("Ivan")
                .email("Ivan@tupopochta.ru")
                .build();

        UserDto createdUser = userService.addUser(userDto);

        UserDto updateUserDto = UserDto.builder()
                .name(null)
                .email("new.ivan@tupopochta.ru")
                .build();

        UserDto updatedUser = userService.updateUser(updateUserDto, createdUser.getId());

        assertEquals("Ivan", updatedUser.getName());
        assertEquals("new.ivan@tupopochta.ru", updatedUser.getEmail());
    }

    @Test
    void updateNonExistentUserThrowsNotFoundExceptionTesting() {
        UserDto updateUserDto = UserDto.builder()
                .name("Ghost User")
                .email("ghost@tupopochta.ru")
                .build();

        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.updateUser(updateUserDto, 9999));
        assertEquals("Пользователь с id 9999 не найден.", exception.getMessage());
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
    void findByIdFailureTestingTesting() {
        NotFoundException n =
                assertThrows(NotFoundException.class, () -> userService.getUserById(1024));
        assertEquals("Пользователь с id 1024 не найден.", n.getMessage());
    }

    @Test
    void addUserWithoutEmailThrowsValidationExceptionTesting() {
        UserDto userDto = UserDto.builder()
                .name("Ivan")
                .email("")
                .build();

        ValidationException exception = assertThrows(ValidationException.class, () -> userService.addUser(userDto));
        assertEquals("У пользователя отсутствует почта!", exception.getMessage());
    }

    @Test
    void addUserWithExistingEmailThrowsConflictExceptionTesting() {
        UserDto userDto1 = UserDto.builder()
                .name("Ivan")
                .email("Ivan@tupopochta.ru")
                .build();
        userService.addUser(userDto1);

        UserDto userDto2 = UserDto.builder()
                .name("Peter")
                .email("Ivan@tupopochta.ru")
                .build();

        ConflictException exception = assertThrows(ConflictException.class, () -> userService.addUser(userDto2));
        assertEquals("Пользователь с таким e-mail уже существует", exception.getMessage());
    }

    @Test
    void updateUserWithSameDataDoesNotChangeUserTesting() {
        UserDto userDto = UserDto.builder()
                .name("Ivan")
                .email("Ivan@tupopochta.ru")
                .build();
        UserDto createdUser = userService.addUser(userDto);

        UserDto updateUserDto = UserDto.builder()
                .name("Ivan")
                .email("Ivan@tupopochta.ru")
                .build();

        UserDto updatedUser = userService.updateUser(updateUserDto, createdUser.getId());

        assertEquals(createdUser.getName(), updatedUser.getName());
        assertEquals(createdUser.getEmail(), updatedUser.getEmail());
    }

    @Test
    void deleteNonExistentUserThrowsNotFoundExceptionTesting() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.deleteUser(1024));
        assertEquals("Пользователь с id 1024 не найден.", exception.getMessage());
    }

    @Test
    void getUsersTesting() {
        UserDto userDto1 = UserDto.builder()
                .name("Ivan")
                .email("Ivan@tupopochta.ru")
                .build();
        UserDto userDto2 = UserDto.builder()
                .name("Peter")
                .email("Peter@tupopochta.ru")
                .build();

        userService.addUser(userDto1);
        userService.addUser(userDto2);

        List<UserDto> users = userService.getUsers();

        assertThat(users, hasSize(2));
        assertThat(users, containsInAnyOrder(
                hasProperty("name", equalTo("Ivan")),
                hasProperty("name", equalTo("Peter"))
        ));
    }

}