package ru.itmo.blpsLab1.data;

import lombok.*;

import javax.persistence.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.ALL;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String password;

    private Long violationsCount = 0L;

    @OneToMany(cascade = ALL, mappedBy = "user")
    @EqualsAndHashCode.Exclude
    private List<Watchlist> watchlists = new ArrayList<>();

    @OneToMany(cascade = ALL, mappedBy = "author")
    @EqualsAndHashCode.Exclude
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(cascade = ALL, mappedBy = "user")
    @EqualsAndHashCode.Exclude
    private List<NotificationRule> notificationRules = new ArrayList<>();

    @OneToMany(cascade = ALL, mappedBy = "user")
    @EqualsAndHashCode.Exclude
    private List<Notification> notifications = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(
                    name = "user_id", referencedColumnName = "id", nullable = false, updatable = false),
            inverseJoinColumns = @JoinColumn(
                    name = "role_id", referencedColumnName = "id", nullable = false, updatable = false))
    @EqualsAndHashCode.Exclude
    private List<Role> roles = new ArrayList<>();

    public static String hashPassword(String password) {
        MessageDigest msgDgst;
        try {
            msgDgst = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            return String.valueOf(password.hashCode());
        }
        msgDgst.update(password.getBytes());
        return new String(msgDgst.digest());
    }
}
