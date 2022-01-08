package ru.itmo.blpsLab1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.blpsLab1.data.User;
import ru.itmo.blpsLab1.data.Watchlist;

import java.util.Optional;
import java.util.Set;

public interface WatchlistRepository extends JpaRepository<Watchlist, Long> {
    Set<Watchlist> findAllByUser(User user);
    Optional<Watchlist> findByIdAndUser(Long id, User user);

    Boolean existsWatchlistByNameAndUser(String name, User user);
}
