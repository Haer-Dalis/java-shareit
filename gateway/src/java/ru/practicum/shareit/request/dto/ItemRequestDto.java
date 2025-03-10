package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ItemRequestDto {

    private Integer id;

    @NotBlank(message = "Description can't be blank")
    private String description;

    private LocalDateTime created;

    private List<ItemDto> items;

}
