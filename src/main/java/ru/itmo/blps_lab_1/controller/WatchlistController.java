package ru.itmo.blps_lab_1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.itmo.blps_lab_1.data.Equity;
import ru.itmo.blps_lab_1.data.NotificationRule;
import ru.itmo.blps_lab_1.data.User;
import ru.itmo.blps_lab_1.data.Watchlist;
import ru.itmo.blps_lab_1.data.dto.*;
import ru.itmo.blps_lab_1.repository.EquityRepository;
import ru.itmo.blps_lab_1.repository.NotificationRuleRepository;
import ru.itmo.blps_lab_1.repository.UserRepository;
import ru.itmo.blps_lab_1.repository.WatchlistRepository;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/me/watchlists")
@Transactional
public class WatchlistController  {
    @Autowired
    UserRepository userRepository;
    @Autowired
    WatchlistRepository watchlistRepository;
    @Autowired
    EquityRepository equityRepository;
    @Autowired
    NotificationRuleRepository notificationRuleRepository;

    @GetMapping("/")
    public ResponseEntity<?> listWatchlists(Principal principal){
        User user = userRepository.findByUsername(principal.getName());

        return new ResponseEntity<List<WatchlistDto>>(WatchlistDto.fromWatchlistsCollection(user.getWatchlists()), HttpStatus.OK);
    }

    @PostMapping("/")
    public ResponseEntity<?> createWatchlist(Principal principal, @RequestBody WatchlistInDto watchlistInDto){
        User user = userRepository.findByUsername(principal.getName());

        if (watchlistRepository.existsWatchlistByNameAndUser(watchlistInDto.getName(), user))
            return new ResponseEntity<String>("Watchlist with specified name already exists", HttpStatus.CONFLICT);

        Watchlist watchlist = new Watchlist();
        watchlist.setName(watchlistInDto.getName());
        watchlist.setUser(user);

        watchlistRepository.save(watchlist);

        return new ResponseEntity<Long>(watchlist.getId(), HttpStatus.CREATED);
    }

    @GetMapping("/{watchlist_id}")
    public ResponseEntity<?> getWatchlist(@PathVariable("watchlist_id") Long id, Principal principal){
        User user = userRepository.findByUsername(principal.getName());
        Optional<Watchlist> watchlist = watchlistRepository.findByIdAndUser(id, user);

        if (!watchlist.isPresent())
            return new ResponseEntity<String>("Watchlist with specified id not found", HttpStatus.NOT_FOUND);

        return new ResponseEntity<WatchlistDto>(WatchlistDto.fromWatchlist(watchlist.get()), HttpStatus.OK);
    }

    @DeleteMapping("/{watchlist_id}")
    public ResponseEntity<?> deleteWatchlist(@PathVariable("watchlist_id") Long id, Principal principal) {
        User user = userRepository.findByUsername(principal.getName());
        Optional<Watchlist> watchlist = watchlistRepository.findByIdAndUser(id, user);

        if (!watchlist.isPresent())
            return new ResponseEntity<String>("Watchlist with specified id not found", HttpStatus.NOT_FOUND);

        watchlistRepository.delete(watchlist.get());

        return new ResponseEntity<Long>(id, HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{watchlist_id}")
    public ResponseEntity<?> updateWatchlist(@PathVariable("watchlist_id") Long id, @RequestBody WatchlistInDto watchlistInDto, Principal principal){
        User user = userRepository.findByUsername(principal.getName());
        Optional<Watchlist> watchlist = watchlistRepository.findByIdAndUser(id, user);

        if (!watchlist.isPresent())
            return new ResponseEntity<String>("Watchlist with specified id not found", HttpStatus.NOT_FOUND);

        if (watchlistRepository.existsWatchlistByNameAndUser(watchlistInDto.getName(), user))
            return new ResponseEntity<String>("Watchlist with specified name already exists", HttpStatus.CONFLICT);

        Watchlist updatedWatchlist = watchlist.get();
        updatedWatchlist.setName(watchlistInDto.getName());

        watchlistRepository.save(updatedWatchlist);
        return new ResponseEntity<WatchlistDto>(WatchlistDto.fromWatchlist(updatedWatchlist), HttpStatus.OK);
    }

    @PostMapping("/{watchlist_id}/equities/{equity_id}")
    public ResponseEntity<?> addEquityToWatchlist(@PathVariable Long watchlist_id, @PathVariable Long equity_id, Principal principal){
        User user = userRepository.findByUsername(principal.getName());

        Optional<Watchlist> watchlist = watchlistRepository.findByIdAndUser(watchlist_id, user);
        if (!watchlist.isPresent())
            return new ResponseEntity<String>("Watchlist with specified id not found", HttpStatus.NOT_FOUND);

        Optional<Equity> equity = equityRepository.findById(equity_id);
        if (!equity.isPresent())
            return new ResponseEntity<String>("Equity with specified id not found", HttpStatus.NOT_FOUND);

        if (watchlist.get().getEquities().contains(equity.get()))
            return new ResponseEntity<String>("Equity with specified id already in watchlist", HttpStatus.CONFLICT);

        Watchlist updatedWatchlist = watchlist.get();
        updatedWatchlist.getEquities().add(equity.get());

        watchlistRepository.save(updatedWatchlist);
        return new ResponseEntity<WatchlistDto>(WatchlistDto.fromWatchlist(updatedWatchlist), HttpStatus.OK);
    }

    @GetMapping("/{watchlist_id}/equities")
    public ResponseEntity<?> listEquitiesFromWatchlist(@PathVariable("watchlist_id") Long id, Principal principal){
        User user = userRepository.findByUsername(principal.getName());

        Optional<Watchlist> watchlist = watchlistRepository.findByIdAndUser(id, user);
        if (!watchlist.isPresent())
            return new ResponseEntity<String>("Watchlist with specified id not found", HttpStatus.NOT_FOUND);

        return new ResponseEntity<List<EquityDto>>(EquityDto.fromEquitiesList(watchlist.get().getEquities()), HttpStatus.OK);
    }

    @DeleteMapping("/{watchlist_id}/equities/{equity_id}")
    public ResponseEntity<?> excludeEquityFromWatchlist(@PathVariable Long watchlist_id, @PathVariable Long equity_id, Principal principal){
        User user = userRepository.findByUsername(principal.getName());

        Optional<Watchlist> watchlist = watchlistRepository.findByIdAndUser(watchlist_id, user);
        if (!watchlist.isPresent())
            return new ResponseEntity<String>("Watchlist with specified id not found", HttpStatus.NOT_FOUND);

        Optional<Equity> equity = equityRepository.findById(equity_id);
        if (!equity.isPresent())
            return new ResponseEntity<String>("Equity with specified id not found", HttpStatus.NOT_FOUND);

        Watchlist updatedWatchlist = watchlist.get();
        if (!updatedWatchlist.getEquities().contains(equity.get()))
            return new ResponseEntity<String>("Equity with specified id not found in watchlist", HttpStatus.NOT_FOUND);

        updatedWatchlist.getEquities().remove(equity.get());

        watchlistRepository.save(updatedWatchlist);
        return new ResponseEntity<WatchlistDto>(WatchlistDto.fromWatchlist(updatedWatchlist), HttpStatus.OK);
    }

    @PostMapping("/{watchlist_id}/equities/{equity_id}/notification")
    public ResponseEntity<?> createNotification(@PathVariable Long watchlist_id, @PathVariable Long equity_id, @RequestBody NotificationRuleInDto notificationRuleInDto, Principal principal) {
        User user = userRepository.findByUsername(principal.getName());

        Optional<Watchlist> watchlist = watchlistRepository.findByIdAndUser(watchlist_id, user);
        if (!watchlist.isPresent())
            return new ResponseEntity<String>("Watchlist with specified id not found", HttpStatus.NOT_FOUND);

        Optional<Equity> equity = equityRepository.findById(equity_id);
        if (!equity.isPresent())
            return new ResponseEntity<String>("Equity with specified id not found", HttpStatus.NOT_FOUND);

        Watchlist updatedWatchlist = watchlist.get();
        if (!updatedWatchlist.getEquities().contains(equity.get()))
            return new ResponseEntity<String>("Equity with specified id not found in watchlist", HttpStatus.NOT_FOUND);

        NotificationRule notificationRule = new NotificationRule();

        notificationRule.setUser(user);
        notificationRule.setEquity(equity.get());
        notificationRule.setOnce(notificationRuleInDto.getOnce());
        notificationRule.setOp(notificationRuleInDto.getOp());
        notificationRule.setValue(notificationRuleInDto.getValue());

        notificationRuleRepository.save(notificationRule);

        return new ResponseEntity<NotificationRuleDto>(NotificationRuleDto.fromNotificationRule(notificationRule), HttpStatus.CREATED);
    }
}
