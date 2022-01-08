package ru.itmo.blpsLab1.controller;

import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.itmo.blpsLab1.data.Comment;
import ru.itmo.blpsLab1.data.User;
import ru.itmo.blpsLab1.data.dto.CommentDto;
import ru.itmo.blpsLab1.data.dto.UserDto;
import ru.itmo.blpsLab1.service.CommentsService;
import ru.itmo.blpsLab1.service.UserService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;
    private final CommentsService commentsService;

    public AdminController(UserService userService, CommentsService commentsService) {
        this.userService = userService;
        this.commentsService = commentsService;
    }

    @GetMapping("/users")
    public ResponseEntity<?> listUsers() {
        List<User> users = userService.findAll();

        return new ResponseEntity<>(UserDto.fromUsersCollection(users), HttpStatus.OK);
    }

    @DeleteMapping("/users/{user_id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long user_id) {
        if (userService.delete(user_id)) {
            return new ResponseEntity<>("User with specified id not found", HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
        }
    }


    @GetMapping("/users/{userId}/comments")
    @ApiOperation(value = "List hidden comments", response = CommentDto.class, responseContainer = "List")
    public ResponseEntity<?> listHiddenComments(@PathVariable Long userId) {
        Optional<List<Comment>> userComments = userService.listHiddenComments(userId);
        if (!userComments.isPresent()) {
            return new ResponseEntity<>("User with specified id not found", HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(CommentDto.fromCommentsCollection(userComments.get()), HttpStatus.OK);
        }
    }

    @Transactional
    @PutMapping("/users/{userId}/comments/{commentId}/publish")
    @ApiOperation(value = "Publish hidden comment", response = CommentDto.class, responseContainer = "List")
    public ResponseEntity<?> publishHiddenComment(@PathVariable Long userId, @PathVariable Long commentId) {
        if (!userService.checkUserExists(userId))
            return new ResponseEntity<>("User with specified id not found", HttpStatus.NOT_FOUND);
        if (!commentsService.checkCommentExists(commentId))
            return new ResponseEntity<>("Comment with specified id not found", HttpStatus.NOT_FOUND);
        Comment comment = commentsService.getById(commentId).get();
        if (!comment.getAuthor().getId().equals(userId))
            return new ResponseEntity<>("Comment with specified id not belongs to specified user", HttpStatus.NOT_FOUND);
        if (comment.isVisible())
            return new ResponseEntity<>("Comment is not hidden", HttpStatus.NOT_FOUND);
        User user = userService.getById(userId).get();

        comment.setVisible(true);
        commentsService.save(comment);

        user.setViolationsCount(user.getViolationsCount() - 1);
        userService.save(user);

        return new ResponseEntity<>(CommentDto.fromComment(comment), HttpStatus.OK);
    }
}
