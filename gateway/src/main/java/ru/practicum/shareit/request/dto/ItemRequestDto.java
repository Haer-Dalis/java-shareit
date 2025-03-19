package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Data
public class ItemRequestDto {
    @NotNull
    private String description;
    private String created;
}
