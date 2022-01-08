package ru.itmo.blpsLab1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.blpsLab1.data.Comment;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Optional<Comment> findById(Long id);
}
