package ru.vsu.zhertvydedlina.aiservice.user.service;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.vsu.zhertvydedlina.aiservice.common.exception.NotFoundException;
import ru.vsu.zhertvydedlina.aiservice.user.component.mapper.UserMapper;
import ru.vsu.zhertvydedlina.aiservice.user.model.entity.User;
import ru.vsu.zhertvydedlina.aiservice.user.model.request.RegisterRequestDto;
import ru.vsu.zhertvydedlina.aiservice.user.service.jwt.JwtService;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService, UserDetailsService {

    public final UserService userService;

    public final JwtService jwtService;

    public final UserMapper userMapper;

    @Override
    public String register(RegisterRequestDto registerDto) {
        if (userService.existsByEmail(registerDto.email())) {
            throw new IllegalArgumentException("User with this email already exists");
        } else if (userService.existsByUsername(registerDto.username())) {
            throw new IllegalArgumentException("User with username already exists");
        }

        User user = userMapper.registerDtoToUser(registerDto);

        User newUser = userService.saveUser(user);

        if (newUser == null) {
            throw new IllegalArgumentException("Server error");
        }

        return jwtService.generateToken(newUser);
    }

    @NotNull
    @Override
    public UserDetails loadUserByUsername(@NotNull String username) throws UsernameNotFoundException {
        try {
            return userService.getUserByUsernameOrEmail(username);
        } catch (NotFoundException e) {
            throw new UsernameNotFoundException(username);
        }
    }
}
