package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.User;

@Data
@Builder
public class ItemDto {
    private Integer id;
    @NotBlank private String name;
    @NotNull private String description;
    @NotNull private Boolean available;
    private User owner;
    private String request;
}
