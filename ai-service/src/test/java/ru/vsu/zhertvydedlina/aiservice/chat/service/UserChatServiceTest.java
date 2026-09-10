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
import ru.vsu.zhertvydedlina.aiservice.chat.component.mapper.ChatMapper;
import ru.vsu.zhertvydedlina.aiservice.chat.component.mapper.MessageMapper;
import ru.vsu.zhertvydedlina.aiservice.chat.model.entity.Chat;
import ru.vsu.zhertvydedlina.aiservice.chat.model.entity.Message;
import ru.vsu.zhertvydedlina.aiservice.chat.model.request.MessageRequestDto;
import ru.vsu.zhertvydedlina.aiservice.chat.model.response.ChatWithMessagesResponseDto;
import ru.vsu.zhertvydedlina.aiservice.chat.model.response.MessageWithFileNamesResponseDto;
import ru.vsu.zhertvydedlina.aiservice.common.exception.BadRequestException;
import ru.vsu.zhertvydedlina.aiservice.file.service.BlueprintFileService;

import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserChatServiceTest {

    @Spy
    private MessageMapper messageMapper = Mappers.getMapper(MessageMapper.class);

    @Spy
    private ChatMapper chatMapper = Mappers.getMapper(ChatMapper.class);

    @Mock
    private ChatService chatService;

    @Mock
    private MessageService messageService;

    @Mock
    private BlueprintFileService blueprintFileService;

    @InjectMocks
    private UserChatServiceImpl userChatService;

    @Test
    @DisplayName("Проверка получения чата по id")
    void shouldReturnChatById() {
        Chat expectedChat = new Chat(1L, 10L);
        when(chatService.getChatById(1L)).thenReturn(expectedChat);

        Chat actualChat = userChatService.getChatById(1L);

        Assertions.assertEquals(expectedChat.getId(), actualChat.getId());
        verify(chatService, times(1)).getChatById(1L);
    }

    @Test
    @DisplayName("Проверка получения списка id чатов пользователя")
    void shouldReturnUserChats() {
        when(chatService.getChatsByUserId(10L)).thenReturn(List.of(new Chat(1L, 10L), new Chat(2L, 10L)));

        List<Long> actualChatIds = userChatService.getUserChats(10L);

        Assertions.assertEquals(List.of(1L, 2L), actualChatIds);
        verify(chatService, times(1)).getChatsByUserId(10L);
    }

    @Test
    @DisplayName("Проверка выброса исключения при null chatId в getChatMessages")
    void shouldThrowExceptionWhenChatIdIsNull() {
        Assertions.assertThrows(
                BadRequestException.class,
                () -> userChatService.getChatMessages(null, 1)
        );
        verifyNoInteractions(chatService, messageService, blueprintFileService);
    }

    @Test
    @DisplayName("Проверка выброса исключения при некорректной странице в getChatMessages")
    void shouldThrowExceptionWhenPageIsInvalid() {
        when(chatService.getChatById(1L)).thenReturn(new Chat(1L, 10L));

        Assertions.assertThrows(
                BadRequestException.class,
                () -> userChatService.getChatMessages(1L, 0)
        );
    }

    @Test
    @DisplayName("Проверка получения сообщений чата")
    void shouldReturnChatMessages() {
        Chat chat = new Chat(1L, 10L);
        Message message = new Message(1L, 1L, 10L, List.of(), "hello", Instant.now());

        when(chatService.getChatById(1L)).thenReturn(chat);
        when(messageService.getMessagesByChatId(1L, PageRequest.of(0, UserChatServiceImpl.DEFAULT_PAGE_SIZE)))
                .thenReturn(List.of(message));
        when(blueprintFileService.getBlueprintFilesByChatId(1L)).thenReturn(List.of());

        ChatWithMessagesResponseDto actualResponse = userChatService.getChatMessages(1L, 1);

        Assertions.assertEquals(chat.getId(), actualResponse.chatId());
        Assertions.assertEquals(1, actualResponse.messages().size());
        verify(chatService, times(1)).getChatById(1L);
        verify(messageService, times(1))
                .getMessagesByChatId(1L, PageRequest.of(0, UserChatServiceImpl.DEFAULT_PAGE_SIZE));
    }

    @Test
    @DisplayName("Проверка выброса исключения при null userId в первом сообщении")
    void shouldThrowExceptionWhenUserIdIsNullOnFirstMessage() {
        MessageRequestDto requestDto = MessageRequestDto.builder()
                .message("hello")
                .filesId(List.of())
                .build();

        Assertions.assertThrows(
                BadRequestException.class,
                () -> userChatService.createChatFromFirstMessage(requestDto)
        );
        verifyNoInteractions(chatService, messageService, blueprintFileService);
    }

    @Test
    @DisplayName("Проверка создания чата из первого сообщения")
    void shouldCreateChatFromFirstMessage() {
        MessageRequestDto requestDto = MessageRequestDto.builder()
                .userId(10L)
                .message("hello")
                .filesId(List.of())
                .build();

        Chat savedChat = new Chat(1L, 10L);
        Message savedMessage = new Message(1L, 1L, 10L, List.of(), "hello", Instant.now());

        when(chatService.saveChat(any(Chat.class))).thenReturn(savedChat);
        when(messageService.saveMessage(any(MessageRequestDto.class))).thenReturn(savedMessage);
        when(blueprintFileService.confirmBlueprintFilesById(requestDto.filesId())).thenReturn(List.of());

        ChatWithMessagesResponseDto actualResponse = userChatService.createChatFromFirstMessage(requestDto);

        Assertions.assertEquals(savedChat.getId(), actualResponse.chatId());
        Assertions.assertEquals(1, actualResponse.messages().size());
        verify(chatService, times(1)).saveChat(any(Chat.class));
        verify(messageService, times(1)).saveMessage(any(MessageRequestDto.class));
    }

    @Test
    @DisplayName("Проверка добавления сообщения в чат")
    void shouldAddMessageToChat() {
        MessageRequestDto requestDto = MessageRequestDto.builder()
                .userId(10L)
                .message("hello")
                .filesId(List.of())
                .build();

        Message savedMessage = new Message(1L, 1L, 10L, List.of(), "hello", Instant.now());

        when(messageService.saveMessage(any(MessageRequestDto.class))).thenReturn(savedMessage);
        when(blueprintFileService.confirmBlueprintFilesById(requestDto.filesId())).thenReturn(List.of());

        MessageWithFileNamesResponseDto actualResponse = userChatService.addMessageToChat(1L, requestDto);

        Assertions.assertEquals(savedMessage.getMessage(), actualResponse.message());
        verify(messageService, times(1)).saveMessage(any(MessageRequestDto.class));
    }

    @Test
    @DisplayName("Проверка удаления чата, если он существует")
    void shouldDeleteChatWhenExists() {
        Chat expectedChat = new Chat(1L, 10L);
        when(chatService.existsById(1L)).thenReturn(true);
        when(chatService.deleteChatById(1L)).thenReturn(expectedChat);

        Chat actualChat = userChatService.deleteChat(1L);

        Assertions.assertEquals(expectedChat.getId(), actualChat.getId());
        verify(messageService, times(1)).deleteMessage(1L);
        verify(blueprintFileService, times(1)).deleteBlueprintFile(1L);
        verify(chatService, times(1)).deleteChatById(1L);
    }

    @Test
    @DisplayName("Проверка удаления чата, если он не существует")
    void shouldReturnNullWhenDeletingNonExistentChat() {
        when(chatService.existsById(1L)).thenReturn(false);

        Chat actualChat = userChatService.deleteChat(1L);

        Assertions.assertNull(actualChat);
        verifyNoInteractions(messageService, blueprintFileService);
        verify(chatService, never()).deleteChatById(anyLong());
    }

    @Test
    @DisplayName("Проверка владельца чата")
    void shouldCheckChatOwner() {
        when(chatService.checkChatOwner(1L, 10L)).thenReturn(true);
        when(chatService.checkChatOwner(1L, 20L)).thenReturn(false);

        Assertions.assertTrue(userChatService.checkChatOwner(1L, 10L));
        Assertions.assertFalse(userChatService.checkChatOwner(1L, 20L));
        verify(chatService, times(1)).checkChatOwner(1L, 10L);
        verify(chatService, times(1)).checkChatOwner(1L, 20L);
    }
}
