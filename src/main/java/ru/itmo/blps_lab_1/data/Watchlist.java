package ru.itmo.blps_lab_1.data;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name="watchlists")
public class Watchlist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    @ManyToMany
    @JoinTable(
            name = "included_equities",
            joinColumns = @JoinColumn(name="watchlist_id", referencedColumnName = "id", nullable = false, updatable = false),
            inverseJoinColumns = @JoinColumn(name="equity_id",  referencedColumnName = "id", nullable = false, updatable = false)
    )
    @EqualsAndHashCode.Exclude
    private List<Equity> equities = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    private User user;
}
