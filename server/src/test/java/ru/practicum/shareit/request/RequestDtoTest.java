package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.ShareItApp;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestOutputDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
@ContextConfiguration(classes = ShareItApp.class)
class RequestDtoTest {

    @Autowired
    private JacksonTester<ItemRequestOutputDto> json;

    @Test
    void testItemRequestDto() throws Exception {

        LocalDateTime created = LocalDateTime.of(2025, 2, 2, 2, 2);

        ItemDto itemDto = ItemDto.builder()
                .requestId(1)
                .name("itemName")
                .description("item description")
                .available(true)
                .build();

        ItemRequestOutputDto itemRequestOutDto = ItemRequestOutputDto.builder()
                .id(1)
                .description("item request 1")
                .created(created)
                .items(List.of(itemDto))
                .build();

        JsonContent<ItemRequestOutputDto> result = json.write(itemRequestOutDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("item request 1");
        assertThat(result).extractingJsonPathStringValue("$.created")
                .isEqualTo(created.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
    }
}