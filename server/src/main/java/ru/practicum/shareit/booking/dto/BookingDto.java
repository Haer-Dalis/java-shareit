package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class BookingDto {

    private Integer itemId;

    private LocalDateTime start;

    private LocalDateTime end;

    private Status status;
}
