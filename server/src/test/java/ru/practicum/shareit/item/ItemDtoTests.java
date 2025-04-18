package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.ShareItApp;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOutputDto;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
@ContextConfiguration(classes = ShareItApp.class)
public class ItemDtoTests {

    @Autowired
    private JacksonTester<ItemDto> jsonShort;

    @Autowired
    private JacksonTester<ItemOutputDto> jsonBig;

    @Test
    void testItemDto() throws Exception {

        ItemDto itemDto = ItemDto.builder()
                .id(1)
                .name("itemName")
                .description("item description")
                .available(true)
                .requestId(1)
                .build();

        JsonContent<ItemDto> result = jsonShort.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("itemName");
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("item description");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);
    }

    @Test
    void testItemOutDto() throws Exception {

        ItemOutputDto itemOutDto = ItemOutputDto.builder()
                .id(1)
                .name("itemName")
                .description("item description")
                .available(true)
                .requestId(1)
                .build();

        JsonContent<ItemOutputDto> result = jsonBig.write(itemOutDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("itemName");
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("item description");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);
    }

}
