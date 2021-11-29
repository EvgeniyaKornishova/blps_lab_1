package ru.itmo.blps_lab_1.data;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name="comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    private String content;
    private LocalDateTime publicationDate = LocalDateTime.now();

    private boolean visible = false;

    @ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    private User author;

    @ManyToOne
    @JoinColumn(name="equity_id", nullable=false)
    private Equity equity;
}
