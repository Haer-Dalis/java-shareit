package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class CommentOutputDto {
    private Integer id;

    private String text;

    private String authorName;

    private LocalDateTime created;
}
