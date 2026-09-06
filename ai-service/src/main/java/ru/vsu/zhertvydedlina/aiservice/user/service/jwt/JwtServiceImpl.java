package ru.vsu.zhertvydedlina.aiservice.user.service.jwt;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vsu.zhertvydedlina.aiservice.user.component.JwtComponent;
import ru.vsu.zhertvydedlina.aiservice.user.entity.User;
import ru.vsu.zhertvydedlina.aiservice.user.service.UserService;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    public final JwtParser jwtParser;

    public final JwtBuilder signedJwtBuilder;
    private final UserService userService;
    private final JwtComponent jwtComponent;

    @Override
    public String extractToken(HttpServletRequest request) {
        return request.getHeader("Authorization");
    }

    @Override
    public Long extractUserId(String token) {
        String parsedUserId = jwtParser.parseSignedClaims(token)
                .getPayload()
                .getSubject();

        if (parsedUserId == null) {
            return null;
        }

        long userId;

        try {
            userId = Long.parseLong(parsedUserId);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("jwt token contains invalid user id");
        }

        return userId;

    }

    @Override
    public String generateToken(User user) {
        return signedJwtBuilder
                .subject(user.getId().toString())
                .claim("username", user.getUsername())
                .claim("email", user.getEmail())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600_000))
                .compact();
    }

    @Override
    public String refreshToken(String refreshToken) {
        if (!isValid(refreshToken)) {
            throw new IllegalArgumentException("refresh token is invalid");
        }

        Long userId = extractUserId(refreshToken);
        User user = userService.getUser(userId);

        return generateToken(user);
    }


    @Override
    public boolean isValid(String token) {
        try {
            jwtParser.parseSignedClaims(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}
