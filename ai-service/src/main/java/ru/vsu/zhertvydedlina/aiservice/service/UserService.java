package ru.vsu.zhertvydedlina.aiservice.service;

import org.springframework.stereotype.Service;
import ru.vsu.zhertvydedlina.aiservice.model.dto.request.AuthDto;
import ru.vsu.zhertvydedlina.aiservice.model.dto.request.RegisterDto;

@Service
public interface UserService {
    Long auth(AuthDto authDto);

    Long register(RegisterDto registerDto);
}
