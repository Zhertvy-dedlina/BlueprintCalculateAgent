package ru.vsu.zhertvydedlina.aiservice.user.service;

import org.springframework.stereotype.Service;
import ru.vsu.zhertvydedlina.aiservice.user.request.AuthRequestDto;
import ru.vsu.zhertvydedlina.aiservice.user.request.RegisterRequestDto;

@Service
public interface UserService {
    Long auth(AuthRequestDto authDto);

    Long register(RegisterRequestDto registerDto);
}
