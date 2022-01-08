package ru.itmo.blpsLab1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.blpsLab1.data.Comment;
import ru.itmo.blpsLab1.repository.CommentRepository;

import javax.xml.bind.ValidationException;
import java.util.Optional;

@Service
public class CommentsService {
    @Autowired
    CommentRepository commentRepository;

    private void linksModeration(Comment comment) throws ValidationException{
        // TODO: add links moderation
    }

    private void capsModeration(Comment comment) throws ValidationException{
        String content = comment.getContent();
        long countCAPSChars = content.chars().filter(Character::isUpperCase).count();
        if (countCAPSChars > 0.75 * content.length())
            throw new ValidationException("Too many CAPS characters");
    }

    private void phonesModeration(Comment comment) throws ValidationException{
        // TODO: add phones moderation
    }

    private void emailsModeration(Comment comment) throws ValidationException{
        // TODO: add emails moderation
    }

    private void languageModeration(Comment comment) throws ValidationException{
        // TODO: add language moderation
    }

    private void blacklistWordsModeration(Comment comment) throws ValidationException{
        // TODO: add blacklist words moderation
    }

    @Transactional
    public void moderateAndPublish(Comment comment) throws ValidationException{
        comment.setVisible(true);

        linksModeration(comment);
        capsModeration(comment);
        phonesModeration(comment);
        emailsModeration(comment);
        languageModeration(comment);
        blacklistWordsModeration(comment);

        commentRepository.save(comment);
    }

    public boolean checkCommentExists(Long commentId) {
        return commentRepository.existsById(commentId);
    }

    public Optional<Comment> getById(Long commentId) {
        return commentRepository.findById(commentId);
    }

    public void save(Comment comment) {
        commentRepository.save(comment);
    }

}
