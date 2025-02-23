package ru.practicum.shareit.item.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.model.Comment;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class CommentMapper {
    public static Comment toComment(CommentDto commentDto) {
        return Comment.builder()
                .text(commentDto.getText())
                .build();
    }

    public static CommentOutputDto toCommentOutputDto(Comment comment) {
        return CommentOutputDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }

    public static List<CommentOutputDto> toCommentOutDtoList(List<Comment> comments) {
        List<CommentOutputDto> result = new ArrayList<>();
        for (Comment comment : comments) {
            result.add(toCommentOutputDto(comment));
        }
        return result;
    }
}
