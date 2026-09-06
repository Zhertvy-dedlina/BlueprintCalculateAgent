package ru.vsu.zhertvydedlina.aiservice.user.service;

import org.springframework.stereotype.Service;
import ru.vsu.zhertvydedlina.aiservice.user.entity.User;

@Service
public interface UserService {
    User getUser(Long userId);

    User getUserByEmail(String email);

    User getUserByUsername(String username);

    User getUserByUsernameOrEmail(String usernameOrEmail);

    User saveUser(User user);

    User updateUser(User user);

    User deleteUser(Long userId);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);
}
