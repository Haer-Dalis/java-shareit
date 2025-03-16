package ru.practicum.shareit.item.dto;

import lombok.Data;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentDto {
    String text;
    Long itemId;
    String authorName;
    LocalDateTime created;
}