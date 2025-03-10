package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.CommentOutputDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class CommentDtoTest {

    @Autowired
    private JacksonTester<CommentOutputDto> json;

    @Test
    void testCommentOutDto() throws Exception {

        UserDto userDto = UserDto.builder()
                .id(1)
                .name("Alex")
                .email("alexFirst@yandex.ru")
                .build();

        CommentOutputDto commentDto = CommentOutputDto.builder()
                .id(1)
                .text("first!")
                .created(LocalDateTime.now())
                .authorName(userDto.getName())
                .build();

        JsonContent<CommentOutputDto> result = json.write(commentDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("first!");
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("Alex");
    }

}
