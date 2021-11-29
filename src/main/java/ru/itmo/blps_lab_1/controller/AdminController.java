package ru.itmo.blps_lab_1.controller;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.itmo.blps_lab_1.data.Comment;
import ru.itmo.blps_lab_1.data.User;
import ru.itmo.blps_lab_1.data.dto.CommentDto;
import ru.itmo.blps_lab_1.data.dto.UserDto;
import ru.itmo.blps_lab_1.repository.CommentRepository;
import ru.itmo.blps_lab_1.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
@Transactional
public class AdminController {
    @Autowired
    UserRepository userRepository;

    @Autowired
    CommentRepository commentRepository;

    @GetMapping("/users")
    public ResponseEntity<?> listUsers(){
        List<User> users = userRepository.findAll();

        return new ResponseEntity<List<UserDto>>(UserDto.fromUsersCollection(users), HttpStatus.OK);
    }

    @DeleteMapping("/users/{user_id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long user_id){
        Optional<User> op_user = userRepository.findById(user_id);
        if (!op_user.isPresent()){
            return new ResponseEntity<String>("User with specified id not found", HttpStatus.NOT_FOUND);
        }
        User user = op_user.get();

        userRepository.delete(user);
        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
    }


    @GetMapping("/users/{user_id}/comments")
    @ApiOperation(value = "List hidden comments", response = CommentDto.class, responseContainer = "List")
    public ResponseEntity<?> listHiddenComments(@PathVariable Long user_id){
        Optional<User> op_user = userRepository.findById(user_id);
        if (!op_user.isPresent()){
            return new ResponseEntity<String>("User with specified id not found", HttpStatus.NOT_FOUND);
        }
        User user = op_user.get();

        List<Comment> comments = user.getComments().stream().filter((c) -> !c.isVisible()).collect(Collectors.toList());

        return new ResponseEntity<List<CommentDto>>(CommentDto.fromCommentsCollection(comments), HttpStatus.OK);
    }

    @PutMapping("/users/{user_id}/comments/{comment_id}/publish")
    @ApiOperation(value = "Publish hidden comment", response = CommentDto.class, responseContainer = "List")
    public ResponseEntity<?> publishHiddenComment(@PathVariable Long user_id, @PathVariable Long comment_id){
        Optional<User> op_user = userRepository.findById(user_id);
        if (!op_user.isPresent())
            return new ResponseEntity<String>("User with specified id not found", HttpStatus.NOT_FOUND);
        User user = op_user.get();

        Optional<Comment> op_comment = commentRepository.findById(comment_id);
        if (!op_comment.isPresent())
            return new ResponseEntity<String>("Comment with specified id not found", HttpStatus.NOT_FOUND);
        Comment comment = op_comment.get();

        if (!comment.getAuthor().getId().equals(user.getId()))
            return new ResponseEntity<String>("Comment with specified id not belongs to specified user", HttpStatus.NOT_FOUND);

        if (comment.isVisible())
            return new ResponseEntity<String>("Comment is not hidden", HttpStatus.NOT_FOUND);

        comment.setVisible(true);
        commentRepository.save(comment);

        user.setViolationsCount(user.getViolationsCount() - 1);
        userRepository.save(user);

        return new ResponseEntity<CommentDto>(CommentDto.fromComment(comment), HttpStatus.OK);
    }
}
