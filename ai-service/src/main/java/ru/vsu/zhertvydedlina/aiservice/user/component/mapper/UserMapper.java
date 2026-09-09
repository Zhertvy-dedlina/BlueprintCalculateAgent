package ru.vsu.zhertvydedlina.aiservice.user.component.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.vsu.zhertvydedlina.aiservice.user.model.entity.User;
import ru.vsu.zhertvydedlina.aiservice.user.model.request.RegisterRequestDto;
import ru.vsu.zhertvydedlina.aiservice.user.model.request.UserUpdateRequestDto;
import ru.vsu.zhertvydedlina.aiservice.user.model.response.UserResponseDto;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    User registerDtoToUser(RegisterRequestDto registerRequestDto);

    UserResponseDto userToUserResponseDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    void updateUserFromDto(UserUpdateRequestDto dto, @MappingTarget User user);
}
