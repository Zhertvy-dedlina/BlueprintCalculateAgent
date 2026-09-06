package ru.vsu.zhertvydedlina.aiservice.user.service.jwt;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import ru.vsu.zhertvydedlina.aiservice.user.entity.User;

@Service
public interface JwtService {
    Long extractUserId(String token);

    String extractToken(HttpServletRequest request);

    String generateToken(User user);

    String refreshToken(String refreshToken);

    boolean isValid(String token);
}
