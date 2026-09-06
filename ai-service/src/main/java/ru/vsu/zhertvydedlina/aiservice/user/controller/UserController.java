package ru.vsu.zhertvydedlina.aiservice.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.vsu.zhertvydedlina.aiservice.user.component.JwtComponent;
import ru.vsu.zhertvydedlina.aiservice.user.entity.User;
import ru.vsu.zhertvydedlina.aiservice.user.request.AuthRequestDto;
import ru.vsu.zhertvydedlina.aiservice.user.request.RegisterRequestDto;
import ru.vsu.zhertvydedlina.aiservice.user.service.AuthService;
import ru.vsu.zhertvydedlina.aiservice.user.service.jwt.JwtService;

@RestController
@RequestMapping("/api/auth")
@Controller
@RequiredArgsConstructor
public class UserController {

    private final AuthenticationManager authenticationManager;

    private final AuthService authService;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AuthRequestDto authRequestDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequestDto.username(),
                        authRequestDto.password()
                )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtService.generateToken((User) userDetails);

        return ResponseEntity.ok(token);
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequestDto registerRequestDto) {
        String token = authService.register(registerRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(token);
    }

    @PostMapping("/refresh")
    public ResponseEntity<String> refresh(@RequestBody String refreshToken) {
        return ResponseEntity.ok(jwtService.refreshToken(refreshToken));
    }

}
