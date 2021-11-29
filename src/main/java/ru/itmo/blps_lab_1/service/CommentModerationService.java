package ru.itmo.blps_lab_1.service;

import bitronix.tm.internal.BitronixRollbackException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.blps_lab_1.data.Comment;
import ru.itmo.blps_lab_1.repository.CommentRepository;

import javax.xml.bind.ValidationException;

@Service
public class CommentModerationService {
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

}
