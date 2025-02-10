package ru.practicum.shareit.user;

import jakarta.validation.constraints.Email;
import lombok.*;

@Builder
@Data
public class User {
    private Integer id;
    private String name;
    @Email private String email;
}
