package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemOutputDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingOutputDto {
    private Integer id;

    private LocalDateTime start;

    private LocalDateTime end;

    private Status status;

    private UserDto booker;

    private ItemOutputDto item;

}
