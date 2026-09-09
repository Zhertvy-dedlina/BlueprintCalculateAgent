package ru.vsu.zhertvydedlina.aiservice.user.service;

import org.springframework.stereotype.Service;
import ru.vsu.zhertvydedlina.aiservice.user.model.request.RegisterRequestDto;

@Service
public interface AuthService {
    String register(RegisterRequestDto registerDto);
}
