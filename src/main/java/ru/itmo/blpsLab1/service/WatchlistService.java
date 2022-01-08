package ru.itmo.blpsLab1.service;

import org.springframework.stereotype.Service;
import ru.itmo.blpsLab1.data.User;
import ru.itmo.blpsLab1.data.Watchlist;
import ru.itmo.blpsLab1.data.dto.WatchlistInDto;
import ru.itmo.blpsLab1.repository.WatchlistRepository;

import java.util.Optional;

@Service
public class WatchlistService {
    private final WatchlistRepository watchlistRepository;

    public WatchlistService(WatchlistRepository watchlistRepository) {
        this.watchlistRepository = watchlistRepository;
    }

    public Optional<Watchlist> findByIdAndUser(Long id, User user) {
        return watchlistRepository.findByIdAndUser(id, user);
    }

    public boolean delete(Long id) {
        if (!watchlistRepository.existsById(id)) {
            return false;
        } else {
            watchlistRepository.deleteById(id);
            return true;
        }
    }

    public boolean existsWatchlistByNameAndUser(String name, User user) {
        return watchlistRepository.existsWatchlistByNameAndUser(name, user);
    }

    public Watchlist updateWatchlist(Long id, WatchlistInDto watchlistInDto) {
        Watchlist updatedWatchlist = watchlistRepository.findById(id).get();
        updatedWatchlist.setName(watchlistInDto.getName());

        watchlistRepository.save(updatedWatchlist);
        return updatedWatchlist;
    }

    public void save(Watchlist watchlist) {
        watchlistRepository.save(watchlist);
    }
}
