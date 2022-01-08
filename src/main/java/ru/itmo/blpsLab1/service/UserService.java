package ru.itmo.blpsLab1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.itmo.blpsLab1.data.Comment;
import ru.itmo.blpsLab1.data.User;
import ru.itmo.blpsLab1.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService{
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public User save(User user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public boolean isAuthorized(String username) {
        return userRepository.findByUsername(username) != null;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public boolean delete(Long userId) {
        Optional<User> opUser = userRepository.findById(userId);
        if (!opUser.isPresent()){
            return false;
        }
        User user = opUser.get();

        userRepository.delete(user);
        return true;
    }

    public Optional<List<Comment>> listHiddenComments(Long userId) {
        Optional<User> opUser = userRepository.findById(userId);
        if (!opUser.isPresent()){
            return Optional.empty();
        }
        User user = opUser.get();

        List<Comment> comments = user.getComments().stream().filter((c) -> !c.isVisible()).collect(Collectors.toList());
        return Optional.of(comments);
    }

    public boolean checkUserExists(Long userId) {
        return userRepository.findById(userId).isPresent();
    }

    public Optional<User> getById(Long userId) {
        return userRepository.findById(userId);
    }

}
