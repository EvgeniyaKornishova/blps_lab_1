package ru.itmo.blpsLab1.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.itmo.blpsLab1.data.Equity;
import ru.itmo.blpsLab1.data.NotificationRule;
import ru.itmo.blpsLab1.data.User;
import ru.itmo.blpsLab1.data.Watchlist;
import ru.itmo.blpsLab1.data.dto.*;
import ru.itmo.blpsLab1.service.EquityService;
import ru.itmo.blpsLab1.service.NotificationService;
import ru.itmo.blpsLab1.service.UserService;
import ru.itmo.blpsLab1.service.WatchlistService;

import java.security.Principal;
import java.util.Optional;

@RestController
@RequestMapping("/me/watchlists")
public class WatchlistController  {
    private final WatchlistService watchlistService;
    private final UserService userService;
    private final EquityService equityService;
    private final NotificationService notificationService;

    public WatchlistController(WatchlistService watchlistService, UserService userService, EquityService equityService, NotificationService notificationService) {
        this.watchlistService = watchlistService;
        this.userService = userService;
        this.equityService = equityService;
        this.notificationService = notificationService;
    }

    @GetMapping("/")
    public ResponseEntity<?> listWatchlists(Principal principal){
        User user = userService.findByUsername(principal.getName());
        if (user == null)
            return new ResponseEntity<>("", HttpStatus.UNAUTHORIZED);

        return new ResponseEntity<>(WatchlistDto.fromWatchlistsCollection(user.getWatchlists()), HttpStatus.OK);
    }

    @PostMapping("/")
    public ResponseEntity<?> createWatchlist(Principal principal, @RequestBody WatchlistInDto watchlistInDto){
        User user = userService.findByUsername(principal.getName());
        if (user == null)
            return new ResponseEntity<>("", HttpStatus.UNAUTHORIZED);

        if (watchlistService.existsWatchlistByNameAndUser(watchlistInDto.getName(), user))
            return new ResponseEntity<>("Watchlist with specified name already exists", HttpStatus.CONFLICT);

        Watchlist watchlist = new Watchlist();
        watchlist.setName(watchlistInDto.getName());
        watchlist.setUser(user);

        watchlistService.save(watchlist);

        return new ResponseEntity<>(watchlist.getId(), HttpStatus.CREATED);
    }

    @GetMapping("/{watchlist_id}")
    public ResponseEntity<?> getWatchlist(@PathVariable("watchlist_id") Long id, Principal principal){
        User user = userService.findByUsername(principal.getName());
        if (user == null)
            return new ResponseEntity<>("", HttpStatus.UNAUTHORIZED);

        Optional<Watchlist> watchlist = watchlistService.findByIdAndUser(id, user);

        if (!watchlist.isPresent())
            return new ResponseEntity<>("Watchlist with specified id not found", HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(WatchlistDto.fromWatchlist(watchlist.get()), HttpStatus.OK);
    }

    @DeleteMapping("/{watchlist_id}")
    public ResponseEntity<?> deleteWatchlist(@PathVariable("watchlist_id") Long id, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        if (user == null)
            return new ResponseEntity<>("", HttpStatus.UNAUTHORIZED);

        if (watchlistService.delete(id)) {
            return new ResponseEntity<>(id, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Watchlist with specified id not found", HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{watchlist_id}")
    public ResponseEntity<?> updateWatchlist(@PathVariable("watchlist_id") Long id, @RequestBody WatchlistInDto watchlistInDto, Principal principal){
        User user = userService.findByUsername(principal.getName());
        if (user == null)
            return new ResponseEntity<>("", HttpStatus.UNAUTHORIZED);

        Optional<Watchlist> watchlist = watchlistService.findByIdAndUser(id, user);

        if (!watchlist.isPresent())
            return new ResponseEntity<>("Watchlist with specified id not found", HttpStatus.NOT_FOUND);

        if (watchlistService.existsWatchlistByNameAndUser(watchlistInDto.getName(), user))
            return new ResponseEntity<>("Watchlist with specified name already exists", HttpStatus.CONFLICT);
        Watchlist updatedWatchlist = watchlistService.updateWatchlist(id, watchlistInDto);
        return new ResponseEntity<>(WatchlistDto.fromWatchlist(updatedWatchlist), HttpStatus.OK);
    }

    @PostMapping("/{watchlist_id}/equities/{equity_id}")
    public ResponseEntity<?> addEquityToWatchlist(@PathVariable Long watchlist_id, @PathVariable Long equity_id, Principal principal){
        User user = userService.findByUsername(principal.getName());
        if (user == null)
            return new ResponseEntity<>("", HttpStatus.UNAUTHORIZED);

        Optional<Watchlist> watchlist = watchlistService.findByIdAndUser(watchlist_id, user);
        if (!watchlist.isPresent())
            return new ResponseEntity<>("Watchlist with specified id not found", HttpStatus.NOT_FOUND);

        Optional<Equity> equity = equityService.findById(equity_id);
        if (!equity.isPresent())
            return new ResponseEntity<>("Equity with specified id not found", HttpStatus.NOT_FOUND);

        if (watchlist.get().getEquities().contains(equity.get()))
            return new ResponseEntity<>("Equity with specified id already in watchlist", HttpStatus.CONFLICT);

        Watchlist updatedWatchlist = watchlist.get();
        updatedWatchlist.getEquities().add(equity.get());

        watchlistService.save(updatedWatchlist);
        return new ResponseEntity<>(WatchlistDto.fromWatchlist(updatedWatchlist), HttpStatus.OK);
    }

    @GetMapping("/{watchlist_id}/equities")
    public ResponseEntity<?> listEquitiesFromWatchlist(@PathVariable("watchlist_id") Long id, Principal principal){
        User user = userService.findByUsername(principal.getName());
        if (user == null)
            return new ResponseEntity<>("", HttpStatus.UNAUTHORIZED);

        Optional<Watchlist> watchlist = watchlistService.findByIdAndUser(id, user);
        if (!watchlist.isPresent())
            return new ResponseEntity<>("Watchlist with specified id not found", HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(EquityDto.fromEquitiesList(watchlist.get().getEquities()), HttpStatus.OK);
    }

    @DeleteMapping("/{watchlistId}/equities/{equityId}")
    public ResponseEntity<?> excludeEquityFromWatchlist(@PathVariable Long watchlistId, @PathVariable Long equityId, Principal principal){
        User user = userService.findByUsername(principal.getName());
        if (user == null)
            return new ResponseEntity<>("", HttpStatus.UNAUTHORIZED);

        Optional<Watchlist> watchlist = watchlistService.findByIdAndUser(watchlistId, user);
        if (!watchlist.isPresent())
            return new ResponseEntity<>("Watchlist with specified id not found", HttpStatus.NOT_FOUND);

        Optional<Equity> equity = equityService.findById(equityId);
        if (!equity.isPresent())
            return new ResponseEntity<>("Equity with specified id not found", HttpStatus.NOT_FOUND);

        Watchlist updatedWatchlist = watchlist.get();
        if (!updatedWatchlist.getEquities().contains(equity.get()))
            return new ResponseEntity<>("Equity with specified id not found in watchlist", HttpStatus.NOT_FOUND);

        updatedWatchlist.getEquities().remove(equity.get());

        watchlistService.save(updatedWatchlist);
        return new ResponseEntity<>(WatchlistDto.fromWatchlist(updatedWatchlist), HttpStatus.OK);
    }

    @PostMapping("/{watchlistId}/equities/{equityId}/notification")
    public ResponseEntity<?> createNotification(@PathVariable Long watchlistId, @PathVariable Long equityId, @RequestBody NotificationRuleInDto notificationRuleInDto, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        if (user == null)
            return new ResponseEntity<>("", HttpStatus.UNAUTHORIZED);

        Optional<Watchlist> watchlist = watchlistService.findByIdAndUser(watchlistId, user);
        if (!watchlist.isPresent())
            return new ResponseEntity<>("Watchlist with specified id not found", HttpStatus.NOT_FOUND);

        Optional<Equity> equity = equityService.findById(equityId);
        if (!equity.isPresent())
            return new ResponseEntity<>("Equity with specified id not found", HttpStatus.NOT_FOUND);

        Watchlist updatedWatchlist = watchlist.get();
        if (!updatedWatchlist.getEquities().contains(equity.get()))
            return new ResponseEntity<>("Equity with specified id not found in watchlist", HttpStatus.NOT_FOUND);

        NotificationRule notificationRule = notificationService.createNotificationRule(notificationRuleInDto, equity.get(), user);
        return new ResponseEntity<>(NotificationRuleDto.fromNotificationRule(notificationRule), HttpStatus.CREATED);
    }
}
