package ru.itmo.blps_lab_1.service;

import ru.itmo.blps_lab_1.data.User;

public interface UserService {
    User save(User user);

    User findByUsername(String username);
}
