package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemDto {

    @Positive(message = "Id must be positive")
    private Integer id;

    @NotBlank(message = "The name can't be empty or contain spaces.")
    private String name;

    @NotBlank(message = "The description can't be blank")
    private String description;

    @NotNull(message = "Available can't be null")
    private Boolean available;

    @Positive(message = "request_id must be positive")
    private Integer requestId;

}
