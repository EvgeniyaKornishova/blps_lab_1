package ru.itmo.blps_lab_1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.itmo.blps_lab_1.data.Notification;
import ru.itmo.blps_lab_1.data.NotificationRule;
import ru.itmo.blps_lab_1.data.User;
import ru.itmo.blps_lab_1.data.dto.NotificationDto;
import ru.itmo.blps_lab_1.data.dto.NotificationRuleDto;
import ru.itmo.blps_lab_1.data.dto.UserDto;
import ru.itmo.blps_lab_1.data.dto.UserInDto;
import ru.itmo.blps_lab_1.repository.RoleRepository;
import ru.itmo.blps_lab_1.service.UserService;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;

@RestController
@Transactional
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    RoleRepository roleRepository;

    @GetMapping("/me")
    public ResponseEntity<?> aboutMe(Principal principal){
        User user = userService.findByUsername(principal.getName());
        if (user == null)
            return new ResponseEntity<String>("", HttpStatus.UNAUTHORIZED);

        return new ResponseEntity<UserDto>(UserDto.fromUser(user), HttpStatus.OK);
    }

    @GetMapping("/me/notifications")
    public ResponseEntity<?> listNotifications(Principal principal) {
        User user = userService.findByUsername(principal.getName());
        if (user == null)
            return new ResponseEntity<String>("", HttpStatus.UNAUTHORIZED);

        List<Notification> notifications = user.getNotifications();

        return new ResponseEntity<List<NotificationDto>>(NotificationDto.fromNotificationsCollection(notifications), HttpStatus.OK);
    }

    @GetMapping("/me/notification_rules")
    public ResponseEntity<?> listNotificationRules(Principal principal) {
        User user = userService.findByUsername(principal.getName());
        if (user == null)
            return new ResponseEntity<String>("", HttpStatus.UNAUTHORIZED);

        List<NotificationRule> notificationRules = user.getNotificationRules();

        return new ResponseEntity<List<NotificationRuleDto>>(NotificationRuleDto.fromNotificationRulesCollection(notificationRules), HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<?> createUser(@RequestBody UserInDto userInDto){
        if (userService.findByUsername(userInDto.getUsername()) != null){
            return new ResponseEntity<>(
                    "User with username " + userInDto.getUsername() + " already exist",
                    HttpStatus.CONFLICT
            );
        }

        User user = new User();
        user.setUsername(userInDto.getUsername());
        user.setPassword(userInDto.getPassword());
        user.setRoles(Arrays.asList(roleRepository.findByName("ROLE_USER")));

        userService.save(user);

        return new ResponseEntity<UserDto>(UserDto.fromUser(user), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> user(Principal principal){
        User user = userService.findByUsername(principal.getName());

        if (user == null)
            return new ResponseEntity<String>("Username or password incorrect", HttpStatus.UNAUTHORIZED);

        return new ResponseEntity<UserDto>(UserDto.fromUser(user), HttpStatus.OK);
    }
}
