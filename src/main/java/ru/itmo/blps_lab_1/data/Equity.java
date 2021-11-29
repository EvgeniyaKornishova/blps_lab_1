package ru.itmo.blps_lab_1.data;

import lombok.*;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static javax.persistence.CascadeType.ALL;

@Entity
@Data
@Table(name="equities")
public class Equity {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String symbol; // Short uniq equity name

    private Float last;
    private Float prev;

    // change = last-prev
    // change% = change / prev * 100

    private Float high;
    private Float low;

    private Float open;

    private Float bid;
    private Float ask;

    private Float extendedHours;
    // extended hours in percents

    private Date nextEarningDate;
    private Time time; // 0 if closed

    private Float vol;

    @ManyToMany(mappedBy = "equities")
    @EqualsAndHashCode.Exclude
    private List<Watchlist> watchlists = new ArrayList<>();

    @OneToMany(cascade = ALL, mappedBy = "equity")
    @EqualsAndHashCode.Exclude
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(cascade = ALL, mappedBy = "equity")
    @EqualsAndHashCode.Exclude
    private Set<NotificationRule> notificationRules = new HashSet<>();
}
