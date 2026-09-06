package ru.vsu.zhertvydedlina.aiservice.user.component.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.vsu.zhertvydedlina.aiservice.user.entity.User;
import ru.vsu.zhertvydedlina.aiservice.user.request.RegisterRequestDto;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    User registerDtoToUser(RegisterRequestDto registerRequestDto);
}
