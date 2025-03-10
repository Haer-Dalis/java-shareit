package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class BookingDto {

    @Positive(message = "ID has to be positive")
    private Integer itemId;

    @NotNull(message = "Start time can't equal null")
    @FutureOrPresent(message = "Start time can only be in the present or future")
    private LocalDateTime start;

    @NotNull(message = "End time can't be null")
    @Future(message = "End time be in the future")
    private LocalDateTime end;

}
