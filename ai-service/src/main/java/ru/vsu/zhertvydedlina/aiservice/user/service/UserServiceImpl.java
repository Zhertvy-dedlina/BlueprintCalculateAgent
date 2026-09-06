package ru.vsu.zhertvydedlina.aiservice.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vsu.zhertvydedlina.aiservice.user.entity.User;
import ru.vsu.zhertvydedlina.aiservice.user.repository.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;


    @Override
    public User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with id" + userId + " not found"));
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User with email" + email + " not found"));
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User with username" + username + " not found"));
    }

    @Override
    public User getUserByUsernameOrEmail(String usernameOrEmail) {
        return userRepository.findByUsernameOrEmail(usernameOrEmail)
                .orElseThrow(() -> new IllegalArgumentException("User with usernameOrEmail" + usernameOrEmail + " not found"));
    }

    @Override
    public User saveUser(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new IllegalArgumentException("User with username" + user.getUsername() + " already exists");
        }

        return userRepository.save(user);
    }

    @Override
    public User updateUser(User user) {
        if (userRepository.findById(user.getId()).isEmpty()) {
            throw new IllegalArgumentException("User with id" + user.getId() + " not found");
        }

        return userRepository.save(user);
    }

    @Override
    public User deleteUser(Long userId) {
        User user = getUser(userId);

        if (user == null) {
            throw new IllegalArgumentException("User with id" + userId + " not found");
        }

        userRepository.delete(user);

        return user;
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
}
