package ru.vsu.zhertvydedlina.aiservice.user.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.vsu.zhertvydedlina.aiservice.user.component.mapper.UserMapper;
import ru.vsu.zhertvydedlina.aiservice.user.model.entity.User;
import ru.vsu.zhertvydedlina.aiservice.user.model.request.UserUpdateRequestDto;
import ru.vsu.zhertvydedlina.aiservice.user.repository.UserRepository;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("Проверка получения пользователя по user")
    void shouldReturnUserById() {
        User expectedUser = new User(1L, "user", "email@mail.ru", "12345678");
        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(expectedUser));

        User actualUser = userService.getUser(1L);

        Assertions.assertEquals(expectedUser.getUsername(), actualUser.getUsername());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Проверка получения пользователя по email")
    void shouldReturnUserByEmail() {
        User expectedUser = new User(1L, "user", "email@mail.ru", "12345678");
        when(userRepository.findByEmail("email@mail.ru")).thenReturn(java.util.Optional.of(expectedUser));

        User actualUser = userService.getUserByEmail("email@mail.ru");

        Assertions.assertEquals(expectedUser.getUsername(), actualUser.getUsername());
        verify(userRepository, times(1)).findByEmail(anyString());
    }

    @Test
    @DisplayName("Проверка получения пользователя по username")
    void shouldReturnUserByUsername() {
        User expectedUser = new User(1L, "user", "email@gmail.ru", "12345678");
        when(userRepository.findByUsername("user")).thenReturn(java.util.Optional.of(expectedUser));

        User actualUser = userService.getUserByUsername("user");

        Assertions.assertEquals(expectedUser.getId(), actualUser.getId());
        verify(userRepository, times(1)).findByUsername(anyString());
    }

    @Test
    @DisplayName("Проверка получения пользователя по username или email")
    void shouldReturnUserByUsernameAndEmail() {
        User expectedUser = new User(1L, "user", "email@gmail.ru", "12345678");
        when(userRepository.findByUsernameOrEmail("user")).thenReturn(java.util.Optional.of(expectedUser));
        when(userRepository.findByUsernameOrEmail("email@gmail.ru")).thenReturn(java.util.Optional.of(expectedUser));

        User actualUserByUsername = userService.getUserByUsernameOrEmail("user");
        User actualUserByEmail = userService.getUserByUsernameOrEmail("email@gmail.ru");

        Assertions.assertEquals(expectedUser.getEmail(), actualUserByUsername.getEmail());
        Assertions.assertEquals(expectedUser.getUsername(), actualUserByEmail.getUsername());
        verify(userRepository, times(2)).findByUsernameOrEmail(anyString());
    }

    @Test
    @DisplayName("Проверка сохранения пользователя")
    void shouldSaveUser() {
        User expectedUser = new User(null, "user", "email@gmail.ru", "12345678");

        when(userRepository.save(expectedUser)).thenReturn(expectedUser);

        User actualUser = userService.saveUser(expectedUser);

        Assertions.assertEquals(expectedUser.getUsername(), actualUser.getUsername());
        verify(userRepository, times(1)).save(expectedUser);
    }

    @Test
    @DisplayName("Проверка обновления пользователя")
    void shouldUpdateUser() {
        User findByIdUser = new User(1L, "user", "email@gmail.ru", "12345678");
        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(findByIdUser));

        User expectedUser = new User(1L, "new-user", "new-email@gmail.ru", "12345678");
        when(userRepository.save(expectedUser)).thenReturn(expectedUser);

        User actualUser = userService.updateUser(
                1L,
                UserUpdateRequestDto.builder()
                        .username("new-user")
                        .email("new-email@gmail.ru")
                        .build()
        );

        Assertions.assertEquals(expectedUser.getUsername(), actualUser.getUsername());
        Assertions.assertEquals(expectedUser.getEmail(), actualUser.getEmail());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Проверка удаления пользователя")
    void shouldDeleteUser() {
        User expectedUser = new User(1L, "user", "email@gmail.ru", "12345678");
        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(expectedUser));

        userService.deleteUser(1L);
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).delete(expectedUser);
    }

    @Test
    @DisplayName("Проверка существования пользователя по email")
    void shouldExistByEmail() {
        User expectedUser = new User(1L, "user", "email@gmail.ru", "12345678");

        when(userRepository.existsByEmail(expectedUser.getEmail())).thenReturn(true);
        when(
                userRepository.existsByEmail(
                        argThat(s -> !s.equals(expectedUser.getEmail()))
                )
        ).thenReturn(false);

        Assertions.assertTrue(userService.existsByEmail(expectedUser.getEmail()));
        Assertions.assertFalse(userService.existsByEmail("nothing"));
        verify(userRepository, times(2)).existsByEmail(anyString());
    }

    @Test
    @DisplayName("Проверка существования пользователя по username")
    void shouldExistByUsername() {
        User expectedUser = new User(1L, "user", "email@gmail.ru", "12345678");

        when(userRepository.existsByUsername(expectedUser.getUsername())).thenReturn(true);
        when(
                userRepository.existsByUsername(
                        argThat(s -> !s.equals(expectedUser.getUsername()))
                )
        ).thenReturn(false);

        Assertions.assertTrue(userService.existsByUsername(expectedUser.getUsername()));
        Assertions.assertFalse(userService.existsByUsername("nothing"));
        verify(userRepository, times(2)).existsByUsername(anyString());
    }
}
