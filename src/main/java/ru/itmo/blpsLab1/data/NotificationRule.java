package ru.itmo.blpsLab1.data;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name="notification_rules")
public class NotificationRule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Boolean once;

    private Double value;

    private CompareOperator op;

    @ManyToOne
    @JoinColumn(name="equity_id", nullable=false)
    private Equity equity;

    @ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    private User user;
}
