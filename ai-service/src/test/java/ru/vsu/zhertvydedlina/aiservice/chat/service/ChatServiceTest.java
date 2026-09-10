package ru.vsu.zhertvydedlina.aiservice.chat.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.vsu.zhertvydedlina.aiservice.chat.model.entity.Chat;
import ru.vsu.zhertvydedlina.aiservice.chat.repository.ChatRepository;
import ru.vsu.zhertvydedlina.aiservice.common.exception.NotFoundException;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChatServiceTest {

    @Mock
    private ChatRepository chatRepository;

    @InjectMocks
    private ChatServiceImpl chatService;

    @Test
    @DisplayName("Проверка получения чата по id")
    void shouldReturnChatById() {
        Chat expectedChat = new Chat(1L, 10L);
        when(chatRepository.findById(1L)).thenReturn(Optional.of(expectedChat));

        Chat actualChat = chatService.getChatById(1L);

        Assertions.assertEquals(expectedChat.getId(), actualChat.getId());
        verify(chatRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Проверка выброса исключения при отсутствии чата")
    void shouldThrowExceptionWhenChatNotFound() {
        when(chatRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> chatService.getChatById(1L));
        verify(chatRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Проверка получения чатов пользователя")
    void shouldReturnChatsByUserId() {
        List<Chat> expectedChats = List.of(new Chat(1L, 10L), new Chat(2L, 10L));
        when(chatRepository.findAllByUserId(10L)).thenReturn(expectedChats);

        List<Chat> actualChats = chatService.getChatsByUserId(10L);

        Assertions.assertEquals(expectedChats.size(), actualChats.size());
        verify(chatRepository, times(1)).findAllByUserId(10L);
    }

    @Test
    @DisplayName("Проверка сохранения чата")
    void shouldSaveChat() {
        Chat expectedChat = new Chat(null, 10L);
        when(chatRepository.save(expectedChat)).thenReturn(expectedChat);

        Chat actualChat = chatService.saveChat(expectedChat);

        Assertions.assertEquals(expectedChat.getUserId(), actualChat.getUserId());
        verify(chatRepository, times(1)).save(expectedChat);
    }

    @Test
    @DisplayName("Проверка удаления чата по id")
    void shouldDeleteChatById() {
        Chat expectedChat = new Chat(1L, 10L);
        when(chatRepository.findById(1L)).thenReturn(Optional.of(expectedChat));

        Chat actualChat = chatService.deleteChatById(1L);

        Assertions.assertEquals(expectedChat.getId(), actualChat.getId());
        verify(chatRepository, times(1)).findById(1L);
        verify(chatRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Проверка существования чата по id")
    void shouldExistById() {
        when(chatRepository.existsById(1L)).thenReturn(true);
        when(chatRepository.existsById(2L)).thenReturn(false);

        Assertions.assertTrue(chatService.existsById(1L));
        Assertions.assertFalse(chatService.existsById(2L));
        verify(chatRepository, times(1)).existsById(1L);
        verify(chatRepository, times(1)).existsById(2L);
    }

    @Test
    @DisplayName("Проверка владельца чата")
    void shouldCheckChatOwner() {
        when(chatRepository.existsChatByIdAndUserId(1L, 10L)).thenReturn(true);
        when(chatRepository.existsChatByIdAndUserId(1L, 20L)).thenReturn(false);

        Assertions.assertTrue(chatService.checkChatOwner(1L, 10L));
        Assertions.assertFalse(chatService.checkChatOwner(1L, 20L));
        verify(chatRepository, times(1)).existsChatByIdAndUserId(1L, 10L);
        verify(chatRepository, times(1)).existsChatByIdAndUserId(1L, 20L);
    }
}
