package ru.itmo.blps_lab_1.data;

import lombok.*;

import javax.persistence.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @OneToMany(cascade = ALL, mappedBy = "user")
    @EqualsAndHashCode.Exclude
    private List<Watchlist> watchlists = new ArrayList<>();

    @OneToMany(cascade = ALL, mappedBy = "user")
    @EqualsAndHashCode.Exclude
    private List<NotificationRule> notificationRules = new ArrayList<>();

    @OneToMany(cascade = ALL, mappedBy = "user")
    @EqualsAndHashCode.Exclude
    private List<Notification> notifications = new ArrayList<>();

    public static String hash_password(String password){
        MessageDigest msgDgst;
        try {
            msgDgst = MessageDigest.getInstance("SHA-256");
        }catch (NoSuchAlgorithmException e){
            return String.valueOf(password.hashCode());
        }
        msgDgst.update(password.getBytes());
        return new String(msgDgst.digest());
    }
}
