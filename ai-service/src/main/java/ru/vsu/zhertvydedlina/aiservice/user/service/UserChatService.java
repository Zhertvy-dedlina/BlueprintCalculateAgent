package ru.vsu.zhertvydedlina.aiservice.user.service;

import org.springframework.stereotype.Service;
import ru.vsu.zhertvydedlina.aiservice.chat.MessageRequestDto;
import ru.vsu.zhertvydedlina.aiservice.chat.response.ChatWithMessagesResponseDto;
import ru.vsu.zhertvydedlina.aiservice.chat.response.MessageWithFileNamesResponseDto;

import java.util.List;

@Service
public interface UserChatService {
    List<Long> getUserChats(Long userId);

    List<ChatWithMessagesResponseDto> getUserChatMessages(Long chatId, int page, int size);

    ChatWithMessagesResponseDto createChatFromFirstMessage(MessageRequestDto message);

    MessageWithFileNamesResponseDto addMessageToChat(Long chatId, MessageRequestDto message);

    Long deleteChat(Long chatId);
}
