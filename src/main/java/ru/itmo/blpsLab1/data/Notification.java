package ru.itmo.blpsLab1.data;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="notification")
@Data
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;

    private LocalDateTime time = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    private User user;
}
