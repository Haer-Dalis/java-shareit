package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class ItemRequestDto {
    private Integer id;
    private String description;
    private Integer requestor;
    private LocalDateTime created;
}
