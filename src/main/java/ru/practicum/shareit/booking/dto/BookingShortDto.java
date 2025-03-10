package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class BookingShortDto {

    private Integer id;

    private Integer bookerId;

    private LocalDateTime start;

    private LocalDateTime end;
}