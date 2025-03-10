package ru.practicum.shareit.item;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.CommentOutputDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.User;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

class CommentMapperTests {

    @Test
    void toCommentTesting() {
        CommentDto commentDto = CommentDto.builder()
                .text("Great item!")
                .build();

        Comment comment = CommentMapper.toComment(commentDto);

        assertNotNull(comment);
        assertEquals(commentDto.getText(), comment.getText());
    }

    @Test
    void toCommentOutputDtoTesting() {
        User author = new User(1, "User1", "user1@example.com");
        Comment comment = Comment.builder()
                .id(1)
                .text("Nice item!")
                .author(author)
                .created(LocalDateTime.now())
                .build();

        CommentOutputDto commentOutputDto = CommentMapper.toCommentOutputDto(comment);

        assertNotNull(commentOutputDto);
        assertEquals(comment.getId(), commentOutputDto.getId());
        assertEquals(comment.getText(), commentOutputDto.getText());
        assertEquals(comment.getAuthor().getName(), commentOutputDto.getAuthorName());
        assertEquals(comment.getCreated(), commentOutputDto.getCreated());
    }

    @Test
    void toCommentOutDtoListTesting() {
        User author1 = new User(1, "User1", "user1@example.com");
        User author2 = new User(2, "User2", "user2@example.com");

        Comment comment1 = Comment.builder()
                .id(1)
                .text("Great item!")
                .author(author1)
                .created(LocalDateTime.now())
                .build();

        Comment comment2 = Comment.builder()
                .id(2)
                .text("Not bad.")
                .author(author2)
                .created(LocalDateTime.now().minusDays(1))
                .build();

        List<Comment> comments = Arrays.asList(comment1, comment2);

        List<CommentOutputDto> commentOutputDtos = CommentMapper.toCommentOutDtoList(comments);
        assertNotNull(commentOutputDtos);
        assertEquals(comments.size(), commentOutputDtos.size());

        CommentOutputDto dto1 = commentOutputDtos.get(0);
        assertEquals(comment1.getId(), dto1.getId());
        assertEquals(comment1.getText(), dto1.getText());
        assertEquals(comment1.getAuthor().getName(), dto1.getAuthorName());

        CommentOutputDto dto2 = commentOutputDtos.get(1);
        assertEquals(comment2.getId(), dto2.getId());
        assertEquals(comment2.getText(), dto2.getText());
        assertEquals(comment2.getAuthor().getName(), dto2.getAuthorName());
    }
}