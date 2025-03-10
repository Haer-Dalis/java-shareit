package ru.practicum.shareit.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {

    boolean existsByEmailAndIdNot(String email, Integer id);

    List<User> findByEmail(String email);
}
