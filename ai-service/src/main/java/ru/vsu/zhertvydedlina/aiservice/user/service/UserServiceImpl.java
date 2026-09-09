package ru.vsu.zhertvydedlina.aiservice.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vsu.zhertvydedlina.aiservice.common.exception.ConflictException;
import ru.vsu.zhertvydedlina.aiservice.common.exception.NotFoundException;
import ru.vsu.zhertvydedlina.aiservice.user.component.mapper.UserMapper;
import ru.vsu.zhertvydedlina.aiservice.user.model.entity.User;
import ru.vsu.zhertvydedlina.aiservice.user.model.request.UserUpdateRequestDto;
import ru.vsu.zhertvydedlina.aiservice.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;


    @Override
    public User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User with email " + email + " not found"));
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User with username " + username + " not found"));
    }

    @Override
    public User getUserByUsernameOrEmail(String usernameOrEmail) {
        return userRepository.findByUsernameOrEmail(usernameOrEmail)
                .orElseThrow(() -> new NotFoundException("User with usernameOrEmail " + usernameOrEmail + " not found"));
    }

    @Override
    public User saveUser(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new ConflictException("User with username " + user.getUsername() + " already exists");
        }

        return userRepository.save(user);
    }

    @Override
    public User updateUser(Long userId, UserUpdateRequestDto update) {
        User user = getUser(userId);

        userMapper.updateUserFromDto(update, user);

        return userRepository.save(user);
    }

    @Override
    public User deleteUser(Long userId) {
        User user = getUser(userId);

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
