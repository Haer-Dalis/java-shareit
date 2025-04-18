package ru.practicum.shareit.item;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@JsonTest
class CommentTest {

    @Test
    void testCommentConstructor() {
        LocalDateTime createdTime = LocalDateTime.now();
        Comment comment = new Comment(1, "Great item!", new Item(), new User(), createdTime);

        assertNotNull(comment);
        assertEquals("Great item!", comment.getText());
        assertEquals(createdTime, comment.getCreated());
        assertNotNull(comment.getItem());
        assertNotNull(comment.getAuthor());
    }

    @Test
    void testBuilderPattern() {
        LocalDateTime createdTime = LocalDateTime.now();
        Comment comment = Comment.builder()
                .id(1)
                .text("Nice product")
                .item(new Item())
                .author(new User())
                .created(createdTime)
                .build();

        assertNotNull(comment);
        assertEquals("Nice product", comment.getText());
        assertEquals(createdTime, comment.getCreated());
    }

    @Test
    void testSettersAndGetters() {
        Comment comment = new Comment();
        comment.setId(1);
        comment.setText("Amazing!");
        comment.setItem(new Item());
        comment.setAuthor(new User());
        comment.setCreated(LocalDateTime.now());

        assertEquals(1, comment.getId());
        assertEquals("Amazing!", comment.getText());
        assertNotNull(comment.getItem());
        assertNotNull(comment.getAuthor());
    }
}