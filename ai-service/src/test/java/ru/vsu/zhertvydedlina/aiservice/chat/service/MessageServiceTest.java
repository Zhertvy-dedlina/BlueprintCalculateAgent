package ru.vsu.zhertvydedlina.aiservice.chat.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.vsu.zhertvydedlina.aiservice.chat.component.mapper.MessageMapper;
import ru.vsu.zhertvydedlina.aiservice.chat.model.entity.Message;
import ru.vsu.zhertvydedlina.aiservice.chat.model.request.MessageRequestDto;
import ru.vsu.zhertvydedlina.aiservice.chat.model.request.MessageUpdateRequestDto;
import ru.vsu.zhertvydedlina.aiservice.chat.repository.MessageRepository;
import ru.vsu.zhertvydedlina.aiservice.common.exception.NotFoundException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MessageServiceTest {

    @Spy
    private MessageMapper messageMapper = Mappers.getMapper(MessageMapper.class);

    @Mock
    private MessageRepository messageRepository;

    @InjectMocks
    private MessageServiceImpl messageService;

    @Test
    @DisplayName("Проверка получения сообщения по id")
    void shouldReturnMessageById() {
        Message expectedMessage = new Message(1L, 1L, 10L, List.of(), "hello", Instant.now());
        when(messageRepository.findById(1L)).thenReturn(Optional.of(expectedMessage));

        Message actualMessage = messageService.getMessageById(1L);

        Assertions.assertEquals(expectedMessage.getMessage(), actualMessage.getMessage());
        verify(messageRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Проверка выброса исключения при отсутствии сообщения")
    void shouldThrowExceptionWhenMessageNotFound() {
        when(messageRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> messageService.getMessageById(1L));
        verify(messageRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Проверка получения сообщений по id чата")
    void shouldReturnMessagesByChatId() {
        PageRequest pageRequest = PageRequest.of(0, 20);
        List<Message> expectedMessages = List.of(
                new Message(1L, 1L, 10L, List.of(), "first", Instant.now()),
                new Message(2L, 1L, 10L, List.of(), "second", Instant.now())
        );
        when(messageRepository.findAllByChatId(1L, pageRequest)).thenReturn(expectedMessages);

        List<Message> actualMessages = messageService.getMessagesByChatId(1L, pageRequest);

        Assertions.assertEquals(expectedMessages.size(), actualMessages.size());
        verify(messageRepository, times(1)).findAllByChatId(1L, pageRequest);
    }

    @Test
    @DisplayName("Проверка сохранения сообщения")
    void shouldSaveMessage() {
        MessageRequestDto requestDto = MessageRequestDto.builder()
                .chatId(1L)
                .userId(10L)
                .message("hello")
                .filesId(List.of())
                .build();

        when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Message actualMessage = messageService.saveMessage(requestDto);

        Assertions.assertEquals(requestDto.message(), actualMessage.getMessage());
        Assertions.assertEquals(requestDto.chatId(), actualMessage.getChatId());
        verify(messageRepository, times(1)).save(any(Message.class));
    }

    @Test
    @DisplayName("Проверка обновления сообщения")
    void shouldUpdateMessage() {
        Message oldMessage = new Message(1L, 1L, 10L, new java.util.ArrayList<>(), "old", Instant.now());
        when(messageRepository.findById(1L)).thenReturn(Optional.of(oldMessage));
        when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MessageUpdateRequestDto updateRequestDto = new MessageUpdateRequestDto(1L, "new", List.of(2L));

        Message actualMessage = messageService.updateMessage(updateRequestDto);

        Assertions.assertEquals("new", actualMessage.getMessage());
        Assertions.assertEquals(List.of(2L), actualMessage.getFilesId());
        verify(messageRepository, times(1)).findById(1L);
        verify(messageRepository, times(1)).save(any(Message.class));
    }

    @Test
    @DisplayName("Проверка удаления сообщения")
    void shouldDeleteMessage() {
        Message expectedMessage = new Message(1L, 1L, 10L, List.of(), "hello", Instant.now());
        when(messageRepository.findById(1L)).thenReturn(Optional.of(expectedMessage));

        Message actualMessage = messageService.deleteMessage(1L);

        Assertions.assertEquals(expectedMessage.getId(), actualMessage.getId());
        verify(messageRepository, times(1)).findById(1L);
        verify(messageRepository, times(1)).delete(expectedMessage);
    }

    @Test
    @DisplayName("Проверка владельца сообщения")
    void shouldCheckMessageOwner() {
        when(messageRepository.existsByIdAndUserId(1L, 10L)).thenReturn(true);
        when(messageRepository.existsByIdAndUserId(1L, 20L)).thenReturn(false);

        Assertions.assertTrue(messageService.checkMessageOwner(1L, 10L));
        Assertions.assertFalse(messageService.checkMessageOwner(1L, 20L));
        verify(messageRepository, times(1)).existsByIdAndUserId(1L, 10L);
        verify(messageRepository, times(1)).existsByIdAndUserId(1L, 20L);
    }
}
