package ru.itmo.blps_lab_1.controller;

import bitronix.tm.internal.BitronixRollbackException;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.itmo.blps_lab_1.data.Comment;
import ru.itmo.blps_lab_1.data.Equity;
import ru.itmo.blps_lab_1.data.User;
import ru.itmo.blps_lab_1.data.dto.CommentDto;
import ru.itmo.blps_lab_1.data.dto.CommentInDto;
import ru.itmo.blps_lab_1.data.dto.EquityDto;
import ru.itmo.blps_lab_1.repository.CommentRepository;
import ru.itmo.blps_lab_1.repository.EquityRepository;
import ru.itmo.blps_lab_1.repository.UserRepository;
import ru.itmo.blps_lab_1.service.CommentModerationService;

import javax.xml.bind.ValidationException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
@RestController
@RequestMapping("/equities")
public class EquityController {

    @Autowired
    EquityRepository equityRepository;
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    CommentModerationService moderationService;

    @GetMapping("/")
    @ApiOperation(value = "Get list of equities", response = EquityDto.class, responseContainer = "List")
    public ResponseEntity<?> listEquities(){
        List<Equity> equities = equityRepository.findAll();
        return new ResponseEntity<List<EquityDto>>(EquityDto.fromEquitiesList(equities), HttpStatus.OK);
    }

    @GetMapping("/{equity_id}")
    @ApiOperation(value = "Get equity by id", response = EquityDto.class)
    public ResponseEntity<?> getEquity(@PathVariable("equity_id") Long id){
        Optional<Equity> equity = equityRepository.findById(id);
        if (!equity.isPresent())
            return new ResponseEntity<String>("Equity with specified id not found", HttpStatus.NOT_FOUND);

       return new ResponseEntity<EquityDto>(EquityDto.fromEquity(equity.get()), HttpStatus.OK);
    }

    @PostMapping("/{equity_id}/comments")
    @ApiOperation(value = "Create comment", response = CommentDto.class)
    public ResponseEntity<?> postComment(@PathVariable("equity_id") Long id, @RequestBody CommentInDto commentInDto, Principal principal){
        User user = userRepository.findByUsername(principal.getName());
        if (user == null)
            return new ResponseEntity<String>("", HttpStatus.UNAUTHORIZED);

        Optional<Equity> equity = equityRepository.findById(id);
        if (!equity.isPresent())
            return new ResponseEntity<String>("Equity with specified id not found", HttpStatus.NOT_FOUND);

        Comment comment = new Comment();
        comment.setEquity(equity.get());
        comment.setContent(commentInDto.getContent());
        comment.setAuthor(user);

        try {
            moderationService.moderateAndPublish(comment);
        } catch (UnexpectedRollbackException | ValidationException e){
            comment.setVisible(false);
            commentRepository.save(comment);
            user.setViolationsCount(user.getViolationsCount() + 1);

            return new ResponseEntity<String>("Comment violates platform rules", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<CommentDto>(CommentDto.fromComment(comment), HttpStatus.OK);
    }

    @GetMapping("/{equity_id}/comments")
    @ApiOperation(value = "List comments", response = CommentDto.class, responseContainer = "List")
    public ResponseEntity<?> listComments(@PathVariable("equity_id") Long id){
        Optional<Equity> equity = equityRepository.findById(id);
        if (!equity.isPresent())
            return new ResponseEntity<String>("Equity with specified id not found", HttpStatus.NOT_FOUND);

        List<Comment> comments = equity.get().getComments().stream().filter(Comment::isVisible).collect(Collectors.toList());

        return new ResponseEntity<List<CommentDto>>(CommentDto.fromCommentsCollection(comments), HttpStatus.OK);
    }
}
