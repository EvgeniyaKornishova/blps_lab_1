package ru.itmo.blpsLab1.controller;

import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.itmo.blpsLab1.data.Comment;
import ru.itmo.blpsLab1.data.Equity;
import ru.itmo.blpsLab1.data.User;
import ru.itmo.blpsLab1.data.dto.CommentDto;
import ru.itmo.blpsLab1.data.dto.CommentInDto;
import ru.itmo.blpsLab1.data.dto.EquityDto;
import ru.itmo.blpsLab1.service.CommentsService;
import ru.itmo.blpsLab1.service.EquityService;
import ru.itmo.blpsLab1.service.UserService;

import javax.xml.bind.ValidationException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/equities")
public class EquityController {

    private final UserService userService;
    private final CommentsService moderationService;
    private final EquityService equityService;
    private final CommentsService commentsService;

    public EquityController(UserService userService, CommentsService moderationService, EquityService equityService, CommentsService commentsService) {
        this.userService = userService;
        this.moderationService = moderationService;
        this.equityService = equityService;
        this.commentsService = commentsService;
    }

    @GetMapping("/")
    @ApiOperation(value = "Get list of equities", response = EquityDto.class, responseContainer = "List")
    public ResponseEntity<?> listEquities() {
        List<Equity> equities = equityService.findAll();
        return new ResponseEntity<>(EquityDto.fromEquitiesList(equities), HttpStatus.OK);
    }

    @GetMapping("/{equity_id}")
    @ApiOperation(value = "Get equity by id", response = EquityDto.class)
    public ResponseEntity<?> getEquity(@PathVariable("equity_id") Long id) {
        Optional<Equity> equity = equityService.findById(id);
        if (!equity.isPresent())
            return new ResponseEntity<>("Equity with specified id not found", HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(EquityDto.fromEquity(equity.get()), HttpStatus.OK);
    }

    @Transactional
    @PostMapping("/{equity_id}/comments")
    @ApiOperation(value = "Create comment", response = CommentDto.class)
    public ResponseEntity<?> postComment(@PathVariable("equity_id") Long id, @RequestBody CommentInDto commentInDto, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        if (user == null)
            return new ResponseEntity<>("", HttpStatus.UNAUTHORIZED);
        Optional<Equity> equity = equityService.findById(id);
        if (!equity.isPresent())
            return new ResponseEntity<>("Equity with specified id not found", HttpStatus.NOT_FOUND);

        Comment comment = new Comment();
        comment.setEquity(equity.get());
        comment.setContent(commentInDto.getContent());
        comment.setAuthor(user);

        try {
            moderationService.moderateAndPublish(comment);
        } catch (UnexpectedRollbackException | ValidationException e) {
            comment.setVisible(false);
            commentsService.save(comment);
            user.setViolationsCount(user.getViolationsCount() + 1);
            userService.save(user);

            return new ResponseEntity<>("Comment violates platform rules", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(CommentDto.fromComment(comment), HttpStatus.OK);
    }

    @GetMapping("/{equity_id}/comments")
    @ApiOperation(value = "List comments", response = CommentDto.class, responseContainer = "List")
    public ResponseEntity<?> listComments(@PathVariable("equity_id") Long id) {
        Optional<Equity> equity = equityService.findById(id);
        if (!equity.isPresent())
            return new ResponseEntity<>("Equity with specified id not found", HttpStatus.NOT_FOUND);

        List<Comment> comments = equity.get().getComments().stream().filter(Comment::isVisible).collect(Collectors.toList());

        return new ResponseEntity<>(CommentDto.fromCommentsCollection(comments), HttpStatus.OK);
    }
}
