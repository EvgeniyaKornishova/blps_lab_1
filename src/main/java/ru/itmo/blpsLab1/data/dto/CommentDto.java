package ru.itmo.blpsLab1.data.dto;

import lombok.Data;
import ru.itmo.blpsLab1.data.Comment;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class CommentDto {
    Long id;

    private String content;
    private LocalDateTime publicationDate;

    private String author;

    public static CommentDto fromComment(Comment comment){
        CommentDto commentDto = new CommentDto();

        commentDto.setId(comment.getId());
        commentDto.setAuthor(comment.getAuthor().getUsername());
        commentDto.setContent(comment.getContent());
        commentDto.setPublicationDate(comment.getPublicationDate());

        return commentDto;
    }

    public static List<CommentDto> fromCommentsCollection(Collection<Comment> comments){
        return comments.stream().map(CommentDto::fromComment).collect(Collectors.toList());
    }
}
