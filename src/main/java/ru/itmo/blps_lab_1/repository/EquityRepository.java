package ru.itmo.blps_lab_1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.blps_lab_1.data.Equity;
import ru.itmo.blps_lab_1.data.Watchlist;

import java.util.Optional;
import java.util.Set;

public interface EquityRepository extends JpaRepository<Equity, Long> {
    Optional<Equity> findById(Long id);
    Set<Equity> getAllByWatchlists(Watchlist watchlist);
}
